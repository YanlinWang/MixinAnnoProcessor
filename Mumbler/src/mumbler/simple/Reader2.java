package mumbler.simple;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

import mumbler.simple.node.BooleanNode;
import mumbler.simple.node.Node;
import mumbler.simple.node.NumberNode;
import mumbler.simple.node.SpecialForm;
import mumbler.simple.node.SymbolNode;
import mumbler.simple.node.MumblerListNode;

public class Reader2 {
    public static MumblerListNode<Eval2> read(InputStream istream) throws IOException {
        return read(new PushbackReader(new InputStreamReader(istream)));
    }

    private static MumblerListNode<Eval2> read(PushbackReader pstream)
            throws IOException {
        List<Eval2> nodes = new ArrayList<Eval2>();

        readWhitespace(pstream);
        char c = (char) pstream.read();
        while ((byte) c != -1) {
            pstream.unread(c);
            nodes.add(readNode(pstream));
            readWhitespace(pstream);
            c = (char) pstream.read();
        }

        return MumblerListNode.list(nodes);
    }

    public static Eval2 readNode(PushbackReader pstream) throws IOException {
        char c = (char) pstream.read();
        pstream.unread(c);
        if (c == '(') {
            return readList(pstream);
        } else if (Character.isDigit(c)) {
            return readNumber(pstream);
        } else if (c == '#') {
            return readBoolean(pstream);
        } else if (c == ')') {
            throw new IllegalArgumentException("Unmatched close paren");
        } else {
            return readSymbol(pstream);
        }
    }

    private static void readWhitespace(PushbackReader pstream)
            throws IOException {
        char c = (char) pstream.read();
        while (Character.isWhitespace(c)) {
            c = (char) pstream.read();
        }
        pstream.unread(c);
    }

    private static SymbolNode2 readSymbol(PushbackReader pstream)
            throws IOException {
        StringBuilder b = new StringBuilder();
        char c = (char) pstream.read();
        while (!(Character.isWhitespace(c) || (byte) c == -1 || c == '(' || c == ')')) {
            b.append(c);
            c = (char) pstream.read();
        }
        pstream.unread(c);
        return new SymbolNode2(b.toString());
    }

    private static Eval2 readList(PushbackReader pstream) throws IOException {
        char paren = (char) pstream.read();
        assert paren == '(' : "Reading a list must start with '('";
        List<Eval2> list = new ArrayList<Eval2>();
        do {
            readWhitespace(pstream);
            char c = (char) pstream.read();

            if (c == ')') {
                // end of list
                break;
            } else if ((byte) c == -1) {
                throw new EOFException("EOF reached before closing of list");
            } else {
                pstream.unread(c);
                list.add(readNode(pstream));
            }
        } while (true);
        MumblerListNode<Node> listCast = new MumblerListNode<Node>();
        for (Node node: list) listCast.cons(node);
        return (Eval2) SpecialForm2.check(MumblerListNode.list(listCast));
    }

    private static NumberNode2 readNumber(PushbackReader pstream)
            throws IOException {
        StringBuilder b = new StringBuilder();
        char c = (char) pstream.read();
        while (Character.isDigit(c)) {
            b.append(c);
            c = (char) pstream.read();
        }
        pstream.unread(c);
        return new NumberNode2(Long.valueOf(b.toString(), 10));
    }

    private static final SymbolNode TRUE_SYM = new SymbolNode("t");
    private static final SymbolNode FALSE_SYM = new SymbolNode("f");

    private static BooleanNode2 readBoolean(PushbackReader pstream)
            throws IOException {
        char hash = (char) pstream.read();
        assert hash == '#' : "Reading a boolean must start with '#'";

        SymbolNode2 sym = readSymbol(pstream);
        if (TRUE_SYM.equals(sym)) {
            return BooleanNode2.TRUE;
        } else if (FALSE_SYM.equals(sym)) {
            return BooleanNode2.FALSE;
        } else {
            throw new IllegalArgumentException("Unknown value: #" + sym.name);
        }
    }
}
