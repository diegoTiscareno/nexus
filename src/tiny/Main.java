/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiny;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;



/**
 *
 * @author diego
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        File file, carpetaTemporal, salidaLexica = null;
//        TinyLexico lexico;
//        Token token;
//        ObjectOutputStream stream = null;
//        if (args.length != 3) {
//            System.out.println("Uso: tiny [nombre de archivo] [-v|-s] [-d|-n]");
//        } else {
//            if ((args[1].equals("-v") || args[1].equals("-s")) &&
//                    (args[2].equals("-d") || args[2].equals("-n"))) {
//                try {
//                    file = new File(args[0]);
//                    lexico = new TinyLexico(file);
//                    if (args[2].equals("-d")) {
//                        carpetaTemporal = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Tiny");
//                        if (!carpetaTemporal.exists()) {
//                            carpetaTemporal.mkdir();
//                        }
//                        salidaLexica = File.createTempFile("lex", null, carpetaTemporal);
//                        stream = new ObjectOutputStream(new FileOutputStream(salidaLexica));
//                    }
//                    
//                    //salidaLexica.deleteOnExit();
//                    
//                    do {
//                        token = lexico.next();
//                        if ("-v".equals(args[1])) {
//                            System.out.println(token);
//                        }
//                        if(args[2].equals("-d")){
//                            stream.writeObject(token.toString());
//                        }
//                    } while (!token.isEofToken());
//                    lexico.cerrarArchivo();
//                    if (args[2].equals("-d")) {
//                        System.out.println(salidaLexica);
//                    }
//                } catch (IOException ex) {
//                    if (ex instanceof FileNotFoundException) {
//                        System.out.println("Error con el archivo: " + ex.getLocalizedMessage());
//                    } else {
//                        System.out.println(ex.getMessage());
//                    }
//                }
//            } else {
//                System.out.println("Uso: tiny [nombre de archivo] [-v|-s] [-d|-n]");
//            }
//        }
        TinyLexico tl;
        TinySintaxis ts;
        TinySemantico tsem;
        List listalex;
        Token tok;
        DefaultMutableTreeNode node;
        try {
            tl = new TinyLexico(new File("/home/diego/operations.tpp"));
            listalex = new ArrayList();
            do {
                tok = tl.next();
                listalex.add(tok);
            } while (tok.getTipoToken() != Token.TipoToken.EOF);
            ts = new TinySintaxis(listalex);
            ts.analize();
            System.out.println(ts.getListaErrores());
            node = ts.getRaiz();
            System.out.println(node.breadthFirstEnumeration());
            tsem = new TinySemantico(node);
            tsem.analyze();
            System.out.println(tsem.getArbolSemantico().breadthFirstEnumeration());
            tsem.getListaErrores();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
