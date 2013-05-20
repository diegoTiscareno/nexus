package tiny;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import util.NodoSemantico;
import util.TablaSimbolos;

/**
 *
 * @author diego
 */
public class TinySemantico {
    private TinySintaxis sintaxis;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode arbolSemantico;
    private DefaultMutableTreeNode nodoActual;
    private int localidad = 0;
    private TablaSimbolos tabla;
    private List<String> listaErrores;
    
    
    public TinySemantico(DefaultMutableTreeNode root) {
        this.root = root;
        arbolSemantico = convertirArbol(root);
        tabla = new TablaSimbolos(511);
        listaErrores = new ArrayList<>();
    }
    
    public void declaracion(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode hijo;
        NodoSemantico nodoSemantico, nodoSemanticoPadre;
        Token token;
        int hijos = nodo.getChildCount();
        nodoSemanticoPadre = (NodoSemantico) nodo.getUserObject();
        token = nodoSemanticoPadre.getToken();
        switch (token.getTipoToken()) {
            case INT:
                nodoSemanticoPadre.setTipo(NodoSemantico.type.INT);
                break;
            case REAL:
                nodoSemanticoPadre.setTipo(NodoSemantico.type.REAL);
                break;
            case VOID:
                nodoSemanticoPadre.setTipo(NodoSemantico.type.VOID);
                break;
            case BOOLEAN:
                nodoSemanticoPadre.setTipo(NodoSemantico.type.BOOLEAN);
                break;
        }
        for(int i = 0; i < hijos; i++) {
            hijo = (DefaultMutableTreeNode) nodo.getChildAt(i);
            nodoSemantico = (NodoSemantico) hijo.getUserObject();
            token = nodoSemantico.getToken();
            nodoSemantico.setTipo(nodoSemanticoPadre.getTipo());
            if (tabla.buscar(token) >= 0) {
                listaErrores.add("Error en la línea " + token.getLinea() +
                    ": Identificador \"" + token.getLexema() + "\" ya declarado declarado.");
            } else {
                tabla.insertar(token, localidad++,"",nodoSemanticoPadre.getTipo());
            }
        }
    }
    
    public void asignacion(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode identificador, expresion;
        NodoSemantico infoid, infoexp;
        Token token =((NodoSemantico)nodo.getUserObject()).getToken();
        identificador = (DefaultMutableTreeNode) nodo.getChildAt(0);
        expresion = (DefaultMutableTreeNode) nodo.getChildAt(1);
        expresion(expresion);
        infoid = (NodoSemantico) identificador.getUserObject();
        infoexp = (NodoSemantico) expresion.getUserObject();
        if (infoid.getTipo() != infoexp.getTipo()) {
            listaErrores.add("Error en la línea " + token.getLinea() +
                ": Tipos incompatibles, \"" + infoid.getToken().getLexema() + "\" es de tipo " 
                + infoid.getTipo() + "," + infoexp.getTipo() + " encontrado");
        } else {
            tabla.insertar(token, localidad++, infoexp.getValor(), infoid.getTipo());
        }

    }
    
    public void expresion(DefaultMutableTreeNode nodo) {
        NodoSemantico infoi, infod, infor;
        DefaultMutableTreeNode derecho, izquierdo;
        infor = ((NodoSemantico)nodo.getUserObject());
        Token token =infor.getToken();
        switch(token.getTipoToken()) {
            case IDENTIFIER:
                if (tabla.buscar(token) < 0) {
                    listaErrores.add("Error en la línea " + token.getLinea() +
                        ": Identificador \"" + token.getLexema() + "\" no declarado.");
                } else {
                    infor.setValor(tabla.valor(token));
                }
                break;
            case NUMBER:
                infor.setValor(token.getLexema());
                infor.setTipo(NodoSemantico.type.INT);
                break;
            case REAL_NUMBER:
                infor.setValor(token.getLexema());
                infor.setTipo(NodoSemantico.type.REAL);
                break;
            case PLUS:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Integer.toString(izq + der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Float.toString(izq + der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case MINUS:
                if (nodo.getChildCount() == 2) {
                    derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                    izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                    expresion(derecho);
                    expresion(izquierdo);
                    infoi = (NodoSemantico) izquierdo.getUserObject();
                    infod = (NodoSemantico) derecho.getUserObject();
                    if ((infoi.getTipo() == NodoSemantico.type.INT
                            && infod.getTipo() == NodoSemantico.type.INT)) {
                        String result;
                        int izq, der;
                        izq = Integer.parseInt(infoi.getValor());
                        der = Integer.parseInt(infod.getValor());
                        result = Integer.toString(izq - der);
                        infor.setValor(result);
                    } else if ((infoi.getTipo() == NodoSemantico.type.REAL
                            && infod.getTipo() == NodoSemantico.type.REAL)) {
                        String result;
                        float izq, der;
                        izq = Float.parseFloat(infoi.getValor());
                        der = Float.parseFloat(infod.getValor());
                        result = Float.toString(izq - der);
                        infor.setValor(result);
                    } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                    }
                } else {
                    izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                    infoi = (NodoSemantico) izquierdo.getUserObject();
                    if (infoi.getTipo() == NodoSemantico.type.REAL) {
                        float izq = Float.parseFloat(infoi.getValor());
                        infor.setValor(Float.toString(-izq));
                    } else if (infoi.getTipo() == NodoSemantico.type.INT) {
                        int izq = Integer.parseInt(infoi.getValor());
                        infor.setValor(Integer.toString(-izq));
                    } else {
                        listaErrores.add("Error en la línea " + token.getLinea() 
                                + ": operador unario \"-\" no aplicable al tipo" + infoi.getTipo());
                    }
                }
                break;
            case ASTERISK:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Integer.toString(izq * der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Float.toString(izq * der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case SLASH:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Integer.toString(izq / der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Float.toString(izq / der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case GREATER_EQUAL:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Boolean.toString(izq >= der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Boolean.toString(izq >= der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case GREATER_THAN:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Boolean.toString(izq > der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Boolean.toString(izq > der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case LESS_EQUAL:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Boolean.toString(izq <= der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Boolean.toString(izq <= der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case LESS_THAN:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Boolean.toString(izq < der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Boolean.toString(izq < der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case EQUALS:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Boolean.toString(izq == der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Boolean.toString(izq == der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.BOOLEAN &&
                        infod.getTipo() == NodoSemantico.type.BOOLEAN)) { 
                    String result;
                    boolean izq, der;
                    izq = Boolean.parseBoolean(infoi.getValor());
                    der = Boolean.parseBoolean(infod.getValor());
                    result = Boolean.toString(izq == der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
            case NOT_EQUALS:
                derecho = (DefaultMutableTreeNode) nodo.getChildAt(1);
                izquierdo = (DefaultMutableTreeNode) nodo.getChildAt(0);
                expresion(derecho);
                expresion(izquierdo);
                infoi = (NodoSemantico) izquierdo.getUserObject();
                infod = (NodoSemantico) derecho.getUserObject();
                if ((infoi.getTipo() == NodoSemantico.type.INT &&
                        infod.getTipo() == NodoSemantico.type.INT) ) {
                    String result;
                    int izq, der;
                    izq = Integer.parseInt(infoi.getValor());
                    der = Integer.parseInt(infod.getValor());
                    result = Boolean.toString(izq != der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.REAL &&
                        infod.getTipo() == NodoSemantico.type.REAL)){
                    String result;
                    float izq, der;
                    izq = Float.parseFloat(infoi.getValor());
                    der = Float.parseFloat(infod.getValor());
                    result = Boolean.toString(izq != der);
                    infor.setValor(result);
                } else if ((infoi.getTipo() == NodoSemantico.type.BOOLEAN &&
                        infod.getTipo() == NodoSemantico.type.BOOLEAN)) { 
                    String result;
                    boolean izq, der;
                    izq = Boolean.parseBoolean(infoi.getValor());
                    der = Boolean.parseBoolean(infod.getValor());
                    result = Boolean.toString(izq != der);
                    infor.setValor(result);
                } else {
                        listaErrores.add("Error en la línea " + token.getLinea() + ": "
                                + infoi.getTipo() + " y " + infod.getTipo() + " son tipos incompatibles");
                }
                break;
        }
    }
    
    
    public void seleccion(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode condicion;
        NodoSemantico info;
        Token token;
        condicion = (DefaultMutableTreeNode) nodo.getChildAt(0);
        expresion(condicion);
        info = (NodoSemantico) condicion.getUserObject();
        token = info.getToken();
        if (info.getTipo() != NodoSemantico.type.BOOLEAN) {
            listaErrores.add("Error en la línea " + token.getLinea() +
                ": Tipos incompatibles, \"boolean\" requerido " 
                + "," + info.getTipo() + " encontrado");
        }
    }
    
    public void iteracion(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode condicion;
        NodoSemantico info;
        Token token;
        condicion = (DefaultMutableTreeNode) nodo.getChildAt(0);
        expresion(condicion);
        info = (NodoSemantico) condicion.getUserObject();
        token = info.getToken();
        if (info.getTipo() != NodoSemantico.type.BOOLEAN) {
            listaErrores.add("Error en la línea " + token.getLinea() +
                ": Tipos incompatibles, \"boolean\" requerido " 
                + "," + info.getTipo() + " encontrado");
        }
    }
    
    public void repeticion(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode condicion;
        NodoSemantico info;
        Token token;
        condicion = (DefaultMutableTreeNode) nodo.getChildAt(1);
        expresion(condicion);
        info = (NodoSemantico) condicion.getUserObject();
        token = info.getToken();
        if (info.getTipo() != NodoSemantico.type.BOOLEAN) {
            listaErrores.add("Error en la línea " + token.getLinea() +
                ": Tipos incompatibles, \"boolean\" requerido " 
                + "," + info.getTipo() + " encontrado");
        }
    }
    
    public void leer(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode identificador;
        NodoSemantico info;
        Token token;
        identificador = (DefaultMutableTreeNode) nodo.getChildAt(0);
        info = (NodoSemantico) identificador.getUserObject();
        token = info.getToken();
        if (info.getTipo() == NodoSemantico.type.INT ||
                info.getTipo() == NodoSemantico.type.REAL){
            if (tabla.buscar(token) >= 0) {
                info.setValor("undefined");
            } else {
                listaErrores.add("Error en la línea " + token.getLinea() +
                    ": Identificador \"" + token.getLexema() + "\" no declarado.");
            }
        } else {
            listaErrores.add("Error en la línea " + token.getLinea() +
                ": No se puede leer el tipo, \"" + info.getTipo() + "\". ");
        }
    }
    
    
    public void analyze() {
        DefaultMutableTreeNode nodo = arbolSemantico;
        DefaultMutableTreeNode hijo;
        NodoSemantico info;
        Token token;
        for (int i = 0; i < nodo.getChildCount(); i++) {
            hijo = (DefaultMutableTreeNode) nodo.getChildAt(i);
            info = (NodoSemantico) hijo.getUserObject();
            token = info.getToken();
            switch (token.getTipoToken()) {
                case INT:
                case REAL:
                case VOID:
                case BOOLEAN:
                    declaracion(hijo);
                    break;
                case ASSIGN:
                    asignacion(hijo);
                    break;
                case IF:
                    seleccion(hijo);
                    break;
                case WHILE:
                    iteracion(hijo);
                    break;
                case DO:
                    repeticion(hijo);
                    break;
                case CIN:
                    leer(hijo);
                    break;
            }
        }
    }
    
    public List<String> getListaErrores() {
        return this.listaErrores;
    }   
    
    public DefaultMutableTreeNode getArbolSemantico() {
        return arbolSemantico;
    }
    
    public static DefaultMutableTreeNode convertirArbol(DefaultMutableTreeNode nodo) {
        DefaultMutableTreeNode clon;
        NodoSemantico nodoSemantico;
        clon = new DefaultMutableTreeNode();
        nodoSemantico = new NodoSemantico();
        nodoSemantico.setToken((Token) nodo.getUserObject());
        clon.setUserObject(nodoSemantico);
        if (!nodo.isLeaf()) {
            for (int i = 0; i < nodo.getChildCount(); i++) {
                clon.add(convertirArbol( (DefaultMutableTreeNode) nodo.getChildAt(i)));
            }
        }
        return clon;
    } 
}
