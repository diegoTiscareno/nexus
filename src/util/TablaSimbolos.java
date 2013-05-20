/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;
import tiny.Token;

/**
 *
 * @author diego
 */
public class TablaSimbolos {
    
    private int tam;
           
    private Object []bucketList;
    
    public TablaSimbolos(int tam) {
        this.tam = tam;
        bucketList = new Object[tam];
    }
    
    public int hash(Token token) {
        int hash = 0;
        char []lex;
        lex = token.getLexema().toCharArray();
        for (int i = 0; i < lex.length; i++) {
            hash = ((hash << 2) + lex[i]) % tam;
        }
        return hash;
    }
    
    public void insertar(Token token, int localidad, String valor, NodoSemantico.type tipo) {
        int hash = hash(token);
        List<NodoSemantico> lista = (List<NodoSemantico>) bucketList[hash];
        
        NodoSemantico registro = new NodoSemantico();
        registro.setLocalidad(localidad);
        registro.setToken(token);
        registro.setValor(valor);
        registro.setTipo(tipo);

        if (lista != null) {
            if (lista.contains(registro)) {
                List<Integer> lineas = lista.get(lista.indexOf(registro)).getListaLineasDeCodigo();
                lineas.add(token.getLinea());
                lista.get(lista.indexOf(registro)).setValor(valor);
            } else {
                ArrayList<Integer> lineasset = new ArrayList<Integer>();
                lineasset.add(token.getLinea());
                registro.setListaLineasDeCodigo(lineasset);
                lista.add(registro);
            }
        } else {
            bucketList[hash] = new ArrayList<NodoSemantico>();
            ArrayList<Integer> lineasset = new ArrayList<Integer>();
            lineasset.add(token.getLinea());
            registro.setListaLineasDeCodigo(lineasset);
            ((List<NodoSemantico>)bucketList[hash]).add(registro);
        }
    }
    
    public int buscar(Token token) {
        int hash = hash(token);
        NodoSemantico registro = new NodoSemantico();
        registro.setToken(token);
        List<NodoSemantico> lista = (List<NodoSemantico>) bucketList[hash];
        if (lista != null) {
            if (lista.contains(registro)) {
                return lista.get(lista.indexOf(registro)).getLocalidad();
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
    
    public String valor(Token token) {
        int hash = hash(token);
        NodoSemantico registro = new NodoSemantico();
        registro.setToken(token);
        List<NodoSemantico> lista = (List<NodoSemantico>) bucketList[hash];
        if (lista != null) {
            if (lista.contains(registro)) {
                return lista.get(lista.indexOf(registro)).getValor();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public NodoSemantico.type tipoToken(Token token) {
        int hash = hash(token);
        List<NodoSemantico> lista = (List<NodoSemantico>) bucketList[hash];
        NodoSemantico registro = new NodoSemantico();
        registro.setToken(token);
        if (lista != null) {
            registro = (NodoSemantico) lista.get(lista.indexOf(registro));
        } else {
            return null;
        }
        return registro.getTipo();
    }
    
    public Object[] getBucketList() {
        return bucketList;
    }
}
