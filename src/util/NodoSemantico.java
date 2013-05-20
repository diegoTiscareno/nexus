package util;

import java.util.ArrayList;
import java.util.List;
import tiny.Token;

/**
 *
 * @author diego
 */
public class NodoSemantico {

    /**
     * @return the localidad
     */
    public int getLocalidad() {
        return localidad;
    }

    /**
     * @param localidad the localidad to set
     */
    public void setLocalidad(int localidad) {
        this.localidad = localidad;
    }

    /**
     * @return the listaLineasCodigo
     */
    public List<Integer> getListaLineasDeCodigo() {
        return listaLineasCodigo;
    }

    /**
     * @param listaLineasCodigo the listaLineasCodigo to set
     */
    public void setListaLineasDeCodigo(List<Integer> listaLineasCodigo) {
        this.listaLineasCodigo = listaLineasCodigo;
    }
    
    public static enum type {
        INT,REAL,BOOLEAN,VOID,UNDEFINED
    }
    
    private int localidad;
    private String valor;
    private type tipo = type.UNDEFINED;
    private Token token;
    private List<Integer> listaLineasCodigo;
    
    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * @return the tipo
     */
    public type getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(type tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the token
     */
    public Token getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(Token token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "Token: " + token.getLexema() + "|" + "Valor: " + valor + "| Tipo: " + tipo; 
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodoSemantico) {
            return ((NodoSemantico)obj).getToken().equals(token);
        } else {
            return false;
        }
    }
}
