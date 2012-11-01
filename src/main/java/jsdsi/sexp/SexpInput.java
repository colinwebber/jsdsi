package jsdsi.sexp;

import java.io.IOException;

public interface SexpInput {
    public Sexp readSexp() throws SexpException, IOException;
}
