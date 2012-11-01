/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jsdsi.AuthCert;
import jsdsi.Certificate;
import jsdsi.Principal;
import jsdsi.Provider;
import jsdsi.Signature;
import jsdsi.StringTag;
import jsdsi.Subject;
import jsdsi.Tag;
import jsdsi.Validity;

/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2005/02/19 17:24:04 $
 *  
 */
public class Keytool {

    static {
        Provider.install();
    }
    
    /**
     *  
     */
    public Keytool() {
        super();
    }

    public static void main(String[] args) {
        try {
            Map commands = getCommands();
            Parser p = new Parser();
            CommandLine line = null;
            try {
                line = p.parse(commands, args);
            } catch (ParsingException e) {
                System.out.println(e.getMessage());
                return;
            }
            if (line == null) {
                return;
            }

            if (line.getCommand().equals("help")) {
                writeHelp(commands);
            } else if (line.getCommand().equals("genkey")) {
                writeValues(line.values);
                Genkey gk = new Genkey(line.values);
                gk.run();
            } else if (line.getCommand().equals("list")) {
                Lister lister = new Lister(line.values);
                lister.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Map getCommands() throws UnknownHostException {
        Map cmds = new HashMap();
        // help
        cmds.put("help",
                new Command("help", "Display this help message.", null));
        // genkey
        Map opts = new HashMap();
        opts.put("alias", new Option("alias", "Key alias.", "mykey"));
        opts.put("keyalg", new Option("keyalg", "Key algorithm.", "rsa-pkcs1"));
        opts.put("keysize", new Option("keysize", "Key size.", "1024"));
        opts.put("hashalg", new Option("hashalg", "Hash algorithm for Signature.", "sha1"));
        opts.put("validity", new Option("validity", "Validity (days).", "28"));
        opts.put("comment", new Option("comment", "Certificate comment.", "SPKI Auto Certificate"));
        InetAddress ia = InetAddress.getLocalHost();
        String s = System.getProperty("user.name") + "@" + ia.getHostName();
        opts.put("tag", new Option("tag", "Auto Certificate Tag value", s));
        opts.put("keypass", new Option("keypass", "Key encryption password.", null));
        opts.put("keystore", new Option("keystore", "KeyStore.", "~/.keystore.sks"));
        opts.put("storepass", new Option("storepass", "KeyStore MAC password.", "password"));
        cmds.put("genkey", new Command("genkey", "Generate a new Key.", opts));
        // list
        opts = new HashMap();
        opts.put("alias", new Option("alias", "Alias to display.", null));
        opts.put("keypass", new Option("keypass", "Key encryption password.", null));
        opts.put("keystore", new Option("keystore", "KeyStore.", "~/.keystore.sks"));
        opts.put("storepass", new Option("storepass", "KeyStore MAC password.", "password"));
        cmds.put("list", new Command("list", "List entry / entries from KeyStore.", opts));
        return cmds;
    }

    private static void writeHelp(Map commands) {
        System.out.println("Keytool usage:");
        
        Iterator it = commands.values().iterator();
        while (it.hasNext()) {
            Command cmd = (Command) it.next();
            System.out.println("\n-"+cmd.key+"\t"+cmd.description);
            Iterator opts = cmd.opts.values().iterator();
            while (opts.hasNext()) {
                Option o = (Option) opts.next();
                System.out.println("\t[-"+o.key+" <"+o.defaultValue+">]\t"+o.description);
            }
        }
    }
    
    private static void writeValues(Map values) {
        Iterator it = values.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            System.out.println(key+" = "+values.get(key));
        }
    }
    private static class Command {
        String key;

        String description;

        Map opts;

        Command(String k, String d, Map o) {
            this.key = k;
            this.description = d;
            if (o == null) {
                o = new HashMap();
            }
            this.opts = o;
        }
    }

    private static class Option {
        String key;

        String description;

        String defaultValue;

        Option(String k, String d, String v) {
            this.key = k;
            this.description = d;
            this.defaultValue = v;
        }
    }

    private static class Parser {
        CommandLine parse(Map cmds, String[] args) {
            if (cmds == null || cmds.size() < 1) {
                return null;
            }
            if (args == null || args.length < 1) {
                throw new ParsingException("No arguments specified.");
            }
            String cmd = args[0];
            if (cmd.length() < 2 || !cmd.startsWith("-")) {
                throw new ParsingException("Illegal command.");
            }
            cmd = cmd.substring(1);
            Command theCmd = (Command) cmds.get(cmd);
            if (theCmd == null) {
                throw new ParsingException("Unknown command: " + cmd);
            }
            Map opts = new HashMap();
            Iterator it = theCmd.opts.values().iterator();
            List list = Arrays.asList(args);
            while (it.hasNext()) {
                Option o = (Option) it.next();
                String val = o.defaultValue;
                int pos = list.indexOf("-"+o.key);
                if (pos>-1) {
                    if (pos == list.size()-1) {
                        throw new ParsingException("No option value specified: "+o.key);
                    }
                    val = (String) list.get(pos+1);
                }
                opts.put(o.key, val);
            }
            return new CommandLine(cmd, opts);
        }
    }

    private static class CommandLine {
        String cmd;

        Map values;

        CommandLine(String c, Map v) {
            this.cmd = c;
            this.values = v;
        }

        String getCommand() {
            return cmd;
        }

        String getValue(String option) {
            return (String) this.values.get(option);
        }
    }

    private static class ParsingException extends RuntimeException {
        ParsingException(String msg) {
            super(msg);
        }
    }
    
    private static abstract class AbstractAction {
        protected Map values;
        AbstractAction(Map v) {
            this.values = v;
        }
        abstract void run() throws Exception;
        protected KeyStore getKeyStore() throws Exception {
            KeyStore store = KeyStore.getInstance("SPKI");
            File file = getKeyStoreFile();
            if (file.exists()) {
                store.load(new FileInputStream(file), getStorePassword());
            } else {
                store.load(null, null);
            }
            return store;
        }
        protected File getKeyStoreFile() throws Exception {
            File file = new File((String) values.get("keystore"));
            if (file.isDirectory()) {
                throw new Exception("Specified keystore is a directory.");
            }
            return file;
        }
        protected char[] getStorePassword() {
            String pwd = (String) values.get("storepass");
            if (pwd == null) {
                return null;
            }
            return pwd.toCharArray();
        }
        protected char[] getKeyPassword() {
            String pwd = (String) values.get("keypass");
            if (pwd == null) {
                return null;
            }
            return pwd.toCharArray();
        }
    }
    private static class Genkey extends AbstractAction {
        Genkey(Map v) {
            super(v);
        }
        void run() throws Exception {
            KeyEnum ke = KeyEnum.fromSpki((String) values.get("keyalg"));
            KeyPair kp = KeyPairFactory.create(ke);
            Certificate cert = createCertificate(kp);
            KeyStore store = getKeyStore();
            String alias = (String) values.get("alias");
            store.setKeyEntry(alias, kp.getPrivate(), getKeyPassword(), new Certificate[] {cert});
            store.store(new FileOutputStream(getKeyStoreFile()), getStorePassword());
        }
        private Certificate createCertificate(KeyPair kp) throws CertificateException {
            Validity v = new Validity(DateUtil.newDate(), DateUtil.newDate());
            Tag tag = new StringTag((String) values.get("tag"));
            String com = (String) values.get("comment");
            AuthCert cert = new AuthCert((Principal)kp.getPublic(), (Subject)kp.getPublic(), v, "text/plan", com, tag, false);
            DigestAlgoEnum dae = DigestAlgoEnum.fromSpki((String) values.get("hashalg"));
            Signature sig = Signature.create(kp, cert, dae);
            return new Certificate(cert, sig);
        }
    }

    private static class Lister extends AbstractAction {
        Lister(Map v) {
            super(v);
        }
        void run() throws Exception {
            KeyStore ks = getKeyStore();
            String alias = (String) values.get("alias");
            if (alias!=null) {
              writeEntry(ks, alias);
            } else {
                Enumeration enum = ks.aliases();
                while (enum.hasMoreElements()) {
                    alias = (String) enum.nextElement();
                    writeEntry(ks, alias);
                }
            }
        }
        private void writeEntry(KeyStore ks, String alias) throws Exception {
            if (ks.isKeyEntry(alias)) {
                writeKeyEntry(ks, alias);
            } else {
                writeCertEntry(ks, alias);
            }
        }
        private void writeKeyEntry(KeyStore ks, String alias) throws Exception {
            Key key = ks.getKey(alias, getKeyPassword());
            Certificate cert = (Certificate) ks.getCertificate(alias);
            System.out.println("****************************************");
            System.out.println("Alias:");
            System.out.println(alias);
            System.out.println("Key:");
            System.out.println(key);
            System.out.println("Certificate:");
            System.out.println(cert);
        }
        private void writeCertEntry(KeyStore ks, String alias) throws Exception {
            Certificate[] certs = (Certificate[]) ks.getCertificateChain(alias);
            System.out.println("****************************************");
            System.out.println("Alias:");
            System.out.println(alias);
            System.out.println("Certificate[]");
            System.out.println(certs);
        }
    }
}