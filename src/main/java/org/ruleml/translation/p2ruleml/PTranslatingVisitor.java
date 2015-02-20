package org.ruleml.translation.p2ruleml;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import javanet.staxutils.IndentingXMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

class PTranslatingVisitor extends PBaseVisitor<Void> {

    private XMLStreamWriter xmlWriter;
    private PParser parser;

    PTranslatingVisitor(Writer out, PParser parser) {
        try {
            xmlWriter =
                new IndentingXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out));
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        this.parser = parser;
    }

	@Override
    public Void visitStart(PParser.StartContext ctx) {
        try {
            xmlWriter.writeStartDocument();
            xmlWriter.writeStartElement("RuleML");
            xmlWriter.writeDefaultNamespace("http://ruleml.org/spec");
            visitChildren(ctx);
            xmlWriter.writeEndElement();
            xmlWriter.flush();
            xmlWriter.close();
            xmlWriter = null;
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
    
	@Override
    public Void visitAsserts(PParser.AssertsContext ctx) {
        writeContext(ctx, "Assert");
        return null;
    }

	@Override
    public Void visitQueries(PParser.QueriesContext ctx) {
        writeContext(ctx, "Query");
        return null;
    }
    
	@Override
    public Void visitAssertion(PParser.AssertionContext ctx) {
        try {
            writeContextWithVars(ctx, "Forall");
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

	@Override
    public Void visitQuery(PParser.QueryContext ctx) {
        try {
            writeContextWithVars(ctx, "Exists");
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

	@Override
    public Void visitAnd(PParser.AndContext ctx) {
        writeContext(ctx, "And");
        return null;
    }

    @Override
    public Void visitImplies(PParser.ImpliesContext ctx) {
        try {
            xmlWriter.writeStartElement("Implies");
            visit(ctx.facts(1));
            visit(ctx.facts(0));
            xmlWriter.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public Void visitNot(PParser.NotContext ctx) {
        writeContext(ctx, "Neg");
        return null;
    }

    @Override
    public Void visitEqual(PParser.EqualContext ctx) {
        writeContext(ctx, "Equal");
        return null;
    }

    @Override
    public Void visitAtom(PParser.AtomContext ctx) {
        writeContext(ctx, "Atom");
        return null;
    }

    @Override
    public Void visitRel(PParser.RelContext ctx) {
        writeContext(ctx, "Rel");
        return null;
    }

    @Override
    public Void visitOp(PParser.OpContext ctx) {
        try {
            xmlWriter.writeCharacters(ctx.getText());
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public Void visitExpr(PParser.ExprContext ctx) {
        writeContext(ctx, "Expr");
        return null;
    }

    @Override
    public Void visitFun(PParser.FunContext ctx) {
        writeContext(ctx, "Fun");
        return null;
    }

	@Override
    public Void visitIndNUM(PParser.IndNUMContext ctx) {
        try {
            xmlWriter.writeStartElement("Ind");
            xmlWriter.writeCharacters(ctx.NUM().getText());
            xmlWriter.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }


	@Override
    public Void visitIndID(PParser.IndIDContext ctx) {
        try {
            xmlWriter.writeStartElement("Ind");
            xmlWriter.writeCharacters(ctx.ID().getText());
            xmlWriter.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

	@Override
    public Void visitIndSQUOTED(PParser.IndSQUOTEDContext ctx) {
        try {
            xmlWriter.writeStartElement("Ind");
            final String text = ctx.SQUOTED().getText();
            xmlWriter.writeCharacters(text.substring(1, text.length() - 1));
            xmlWriter.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public Void visitVar(PParser.VarContext ctx) {
        try {
            xmlWriter.writeStartElement("Var");
            xmlWriter.writeCharacters(ctx.ID().getText());
            xmlWriter.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    private void writeContextWithVars(ParserRuleContext ctx, String quantifier) throws XMLStreamException {
        final Set<String> varSet = new HashSet<>();
        for (final ParseTree node : XPath.findAll(ctx, "//var/ID", parser)) {
            varSet.add(node.getText());
        }
        if (varSet.isEmpty()) {
            visitChildren(ctx);
        } else {
            xmlWriter.writeStartElement(quantifier);
            for (final String var : varSet) {
                xmlWriter.writeStartElement("Var");
                xmlWriter.writeCharacters(var);
                xmlWriter.writeEndElement();
            }
            visitChildren(ctx);
            xmlWriter.writeEndElement();
        }
    }

    private void writeContext(ParserRuleContext ctx, String name) {
        try {
            xmlWriter.writeStartElement(name);
            visitChildren(ctx);
            xmlWriter.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

}
