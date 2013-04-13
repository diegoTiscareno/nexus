
package IDE;

/**
 *
 * @author diego
 */
class TokenAt{
            private int tokenAt;
            private int tokenEnd;
            
            public TokenAt(int ta, int te){
                tokenAt=ta;
                tokenEnd=te;
            }
            
            @Override
            public String toString(){
                return "(" + tokenAt + "," + tokenEnd + ")";
            }

            public int getTokenAt() {
                return tokenAt;
            }

            public int getTokenEnd() {
                return tokenEnd;
            }
        }