/* Generated By:JJTree: Do not edit this line. ASTCFCMethodCallNode.java */

package org.cfeclipse.cfml.parser.cfscript;

public class ASTCFCMethodCallNode extends SimpleNode {
	String name;
  public ASTCFCMethodCallNode(int id) {
    super(id);
  }
  
  public String toString(String prefix) {
  	return super.toString(prefix) + " hello, my name is " + name;
  }
}
