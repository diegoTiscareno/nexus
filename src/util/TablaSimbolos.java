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
    
    private class Registro { 
        //<editor-fold defaultstate="collapsed" desc="Registro para la bucketList">
        private int localidad;
        private Token token;
        private List<Integer> listaLineasDeCodigo;
        
        public int getLocalidad() {
            return localidad;
        }
        
        public void setLocalidad(int localidad) {
            this.localidad = localidad;
        }
        
        public Token getToken() {
            return token;
        }
        
        public void setToken(Token token) {
            this.token = token;
        }
        
        public List<Integer> getListaLineasDeCodigo() {
            return listaLineasDeCodigo;
        }
        
        public void setListaLineasDeCodigo(List<Integer> list) {
            this.listaLineasDeCodigo = list;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Registro) {
                Registro reg = (Registro) obj;
                return token.equals(reg.getToken());
            } else {
                return false;
            }
                
        }
        //</editor-fold>
    }
        
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
    
    public void insertar(Token token, int localidad) {
        int hash = hash(token);
        List<Registro> lista = (List<Registro>) bucketList[hash];
        
        Registro registro = new Registro();
        registro.setLocalidad(localidad);
        registro.setToken(token);

        if (lista != null) {
            if (lista.contains(registro)) {
                List<Integer> lineas = lista.get(lista.indexOf(registro)).getListaLineasDeCodigo();
                lineas.add(token.getLinea());
            } else {
                ArrayList<Integer> lineasset = new ArrayList<Integer>();
                lineasset.add(token.getLinea());
                registro.setListaLineasDeCodigo(lineasset);
                lista.add(registro);
            }
        } else {
            bucketList[hash] = new ArrayList<Registro>();
            ArrayList<Integer> lineasset = new ArrayList<Integer>();
            lineasset.add(token.getLinea());
            registro.setListaLineasDeCodigo(lineasset);
            ((List<Registro>)bucketList[hash]).add(registro);
        }
    }
    
    public int buscar(Token token) {
        int hash = hash(token);
        List<Registro> lista = (List<Registro>) bucketList[hash];
        if (lista.contains(token)) {
            return lista.get(lista.indexOf(token)).getLocalidad();
        } else {
            return -1;
        }
    }
}
