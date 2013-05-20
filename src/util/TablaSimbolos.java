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
        private String valor;
        private Token token;
        private NodoSemantico.type tipo;
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
        
        public void setValor(String valor) {
            this.valor = valor;
        }
        
        public String getValor() {
            return this.valor;
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

        /**
         * @return the tipo
         */
        public NodoSemantico.type getTipo() {
            return tipo;
        }

        /**
         * @param tipo the tipo to set
         */
        public void setTipo(NodoSemantico.type tipo) {
            this.tipo = tipo;
        }
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
    
    public void insertar(Token token, int localidad, String valor, NodoSemantico.type tipo) {
        int hash = hash(token);
        List<Registro> lista = (List<Registro>) bucketList[hash];
        
        Registro registro = new Registro();
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
            bucketList[hash] = new ArrayList<Registro>();
            ArrayList<Integer> lineasset = new ArrayList<Integer>();
            lineasset.add(token.getLinea());
            registro.setListaLineasDeCodigo(lineasset);
            ((List<Registro>)bucketList[hash]).add(registro);
        }
    }
    
    public int buscar(Token token) {
        int hash = hash(token);
        Registro registro = new Registro();
        registro.setToken(token);
        List<Registro> lista = (List<Registro>) bucketList[hash];
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
        Registro registro = new Registro();
        registro.setToken(token);
        List<Registro> lista = (List<Registro>) bucketList[hash];
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
        List<Registro> lista = (List<Registro>) bucketList[hash];
        Registro registro = new Registro();
        registro.setToken(token);
        if (lista != null) {
            registro = (Registro) lista.get(lista.indexOf(registro));
        } else {
            return null;
        }
        return registro.getTipo();
    }
}
