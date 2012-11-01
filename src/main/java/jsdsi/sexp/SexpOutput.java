package jsdsi.sexp;

import java.io.IOException;

public interface SexpOutput {
    public void writeSexp(Sexp s) throws IOException;
    public void flush() throws IOException;
}
