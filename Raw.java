package org.scotek.vpl;

import java.util.*;
import javax.swing.*;		// for number input
import java.lang.reflect.*;

interface Expression
{
   public Object getValue();
}

abstract class Statement
{
   abstract public void performAction();
}


class LiteralInteger implements Expression
{
   private int val;

   LiteralInteger(int v)
   {
      val = v;
   }

   public Object getValue()
   {
      return val;
   }
}

class LiteralString implements Expression
{
   private String str;

   LiteralString(String s)
   {
      str = s;
   }

   public Object getValue()
   {
      return str;
   }
}

class LiteralBoolean implements Expression
{
   private Boolean bool;

   LiteralBoolean(boolean b)
   {
      bool = b;
   }

   public Object getValue()
   {
      return bool;
   }
}
//////////////////////////////////////////////////////////////////////


abstract class NaryOperandOperator implements Expression
{
   protected Expression[] exps;

   protected NaryOperandOperator(int nary)
   {
      exps = new Expression[nary];
   }

   public void setOperand(int n, Expression e)
   {
      exps[n] = e;
   }
   /** Throws NotEnoughArgumentsException is this operand is null. */
   public Expression getOperand(int n) throws NotEnoughArgumentsException
   {
      if(n > exps.length)
	 throw new RuntimeException("Invalid use of NaryOperandOperator in RAW, operand " + n + " too large (size " + exps.length + ")");

      if(exps[n] == null)
	 throw new NotEnoughArgumentsException("Empty space!", this);

      return exps[n];
   }

   public abstract Object getValue();
}

abstract class TwoOperandOperator extends NaryOperandOperator
{
   public TwoOperandOperator()
   {
      super(2);
   }

    // compatability wrappers for exisiting code
   /** @deprecated use setOperand(0, ...) instead. */
   public void setLeftOperand(Expression left)
   {
      exps[0] = left;
   }
   /** @deprecated use setOperand(1, ...) instead. */
   public void setRightOperand(Expression right)
   {
      exps[1] = right;
   }
}

class AdditionOperator extends TwoOperandOperator
{
   public AdditionOperator()  {  super();  }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();
      
      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l + r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

class SubtractionOperator extends TwoOperandOperator
{
   public SubtractionOperator() { super(); }
   
   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l - r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}
   
class MultiplicationOperator extends TwoOperandOperator
{
   public MultiplicationOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l * r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

class DivisionOperator extends TwoOperandOperator
{
   public DivisionOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 if(r.intValue() == 0)
	 {
	    throw new ArithmeticException();
	 }

	 return l / r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

class AndOperator extends TwoOperandOperator
{
   public AndOperator() { super(); }
   
   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Boolean && rVal instanceof Boolean)
      {
	 Boolean l = (Boolean)lVal;
	 Boolean r = (Boolean)rVal;

	 return l && r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}
   

class OrOperator extends TwoOperandOperator
{
   public OrOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Boolean && rVal instanceof Boolean)
      {
	 Boolean l = (Boolean)lVal;
	 Boolean r = (Boolean)rVal;

	 return l || r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

// this is different because it is a unary operator
class NotOperator implements Expression
{
   private Expression operand;

   public void setOperand(Expression op)
   {
      operand = op;
   }

   public Object getValue()
   {
      if(operand == null)
	 throw new NotEnoughArgumentsException("Empty space.", this);

      Object val = operand.getValue();

      if(val instanceof Boolean)
      {
	 Boolean o = (Boolean)val;

	 return !o;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}


class ModulusOperator extends TwoOperandOperator
{
   public ModulusOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l % r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}






class EqualsOperator extends TwoOperandOperator
{
   public EqualsOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Boolean && rVal instanceof Boolean)
      {
	 Boolean l = (Boolean)lVal;
	 Boolean r = (Boolean)rVal;

	 //Do we use == or .equals here? Since we have autoboxing everywhere I think it's safe to .equals()
	 return l.equals(r);	       
      }
      else if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l.equals(r);
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}



class GreaterOperator extends TwoOperandOperator
{
   public GreaterOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l > r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
 }

class LesserOperator extends TwoOperandOperator
{
   public LesserOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof Integer && rVal instanceof Integer)
      {
	 Integer l = (Integer)lVal;
	 Integer r = (Integer)rVal;

	 return l < r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}


class RAWException extends RuntimeException
{
   protected Object errorLoc;

   /** @depreciated */
   RAWException() { super(); }	// TODO remove
   /** @depreciated */
   RAWException(String s) { super(s); } // TODO remove
   RAWException(String s, Object o)
   {
      this(s);
      errorLoc = o;
   }

   public Object getErrorLocation()
   {
      return errorLoc;
   }
}
class InvalidArgumentTypeException extends RAWException
{
   InvalidArgumentTypeException() { super(); }
   InvalidArgumentTypeException(String s) { super(s); }
   InvalidArgumentTypeException(String s, Object o)  { super(s, o); }
}

/*class InvalidArgumentTypeException extends RuntimeException
{
   public InvalidArgumentTypeException() { super(); }
   public InvalidArgumentTypeException(String s) { super(s); }

   // EEH test for highlighting error where exception happens
//   WorkspaceObject wo;
   Object errorLoc;
   public InvalidArgumentTypeException(String s, Object o)//WorkspaceObject w)
   {
      super(s);
//      wo = w;
      errorLoc = o;
   }
//   public WorkspaceObject getWorkspaceObject()
//   {
//      return wo;
//   }
   public Object getErrorLocation()
   {
      return errorLoc;
   }
   }*/
 //class NotEnoughArgumentsException extends RuntimeException {}
class NotEnoughArgumentsException extends RAWException
{
   NotEnoughArgumentsException() { super(); }
   NotEnoughArgumentsException(String s, Object o) { super(s, o); }
}
class UndefinedVariableException extends RAWException
{
   UndefinedVariableException(String s)
   {
      super(s);
   }
}
class UndefinedAlgorithmException extends RAWException
{
   UndefinedAlgorithmException(String s)
   {
      super(s);
   }
}

class InputNumber implements Expression
{
   private Expression prompt;

   public void setPrompt(Expression s)
   {
      prompt = s;
   }

   public Object getValue()
   {
      if(prompt == null)
	 throw new NotEnoughArgumentsException("Prompt missing from InputNumber block.", this);
      Object pVal = prompt.getValue();

      return enterNumber("" + pVal);
   }

   private Integer enterNumber(String p)
   {
      String retVal = JOptionPane.showInputDialog(p);
/* WHY WAS THIS PUT IN???
      if(retVal == null)
	 retVal = "";
*/

      if(retVal == null)
      {
//	 return enterNumber(p);
	 throw new ExecutionAbortedException();
      }
      else
      {
	 try
	 {
	    Integer n = Integer.parseInt(retVal);
	    return n;
	 }
	 catch(NumberFormatException nfe)
	 {
	    return enterNumber(p);
	 }
      }
   }
}

// generates a random number between [0..max] inclusive.
class RandomInteger implements Expression
{
   private Expression max;

   public void setMax(Expression e)
   {
      max = e;
   }

   public Expression getMax()
   {
      return max;
   }
   
   public Object getValue()
   {
      if(max == null)
	 throw new NotEnoughArgumentsException("Max value missing from RandomInteger", this);

      Object mObj = max.getValue();
      if(mObj instanceof Integer)
      {
	 int m = (Integer)mObj;
	 double r = Math.random() * (m + 1);
	 return (int)r;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

class PrintStatement extends Statement
{
   private Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public void performAction()
   {
      if(exp == null)
      {
	 throw new NotEnoughArgumentsException("Print expression cannot be empty.", this);
      }
      else
      {
	 Object v = exp.getValue();
	 System.out.println("" + v);
      }
   }
}

// treats both args as strings, concats them, and returns them.
class CombineStringOperator extends TwoOperandOperator
{
   public CombineStringOperator() { super(); }

   public Object getValue()
   {
      Object lVal = getOperand(0).getValue();
      Object rVal = getOperand(1).getValue();

      if(lVal instanceof LinkedList || rVal instanceof LinkedList)
      {      
	 throw new InvalidArgumentTypeException();
      }
      else
      {
	 return lVal.toString() + rVal.toString();
      }
   }
}

class StatementSequence extends Statement
{
   private Vector<Statement> statements = new Vector<Statement>();

   void addStatement(Statement s)
   {
      statements.add(s);
   }

   void setAllStatements(Vector<Statement> ss)
   {
      statements = ss;
   }

   Vector<Statement> getAllStatements()
   {
      return statements;
   }

   public void performAction()
   {
      for(Statement s : statements)
      {
	 if(s == null)
	    throw new NotEnoughArgumentsException("Empty space in StatementSequence.", this);
	 s.performAction();
      }
   }
}


class AlgorithmStart extends Statement
{
   private Statement algorithmBody;
//   String[] algorithmFormalArgs;

   public void setBody(Statement s)
   {
      algorithmBody = s;
   }

   public void performAction()
   {
      if(algorithmBody == null)
	 throw new NotEnoughArgumentsException("Empty space in algorithm body.", this);
      else
	 algorithmBody.performAction();

   }
}

 // To return multiple values the user must explicitly create a tuple.
class ReturnStatement extends Statement
{
   private Expression returnValue;
   
   public void setReturnValue(Expression e)
   {
      returnValue = e;
   }

   // This will never return normally.  Instead control will be regained in the Algorithm object this is part of.
   public void performAction()
   {
      if(returnValue == null)
      {
	 throw new NotEnoughArgumentsException("Empty space in return block.", this);
      }
      else
      {
	 Object result = returnValue.getValue();
	 if(result == null)
	 {
	    throw new ReturnFromAlgorithm(null);
	 }
	 else
	 {
	    throw new ReturnFromAlgorithm(result);
	 }
      }
   }
}

class ReturnFromAlgorithm extends RuntimeException
{
   private Object returnValue;
   public ReturnFromAlgorithm(Object v)   { returnValue = v;  }
   public Object getReturnValue()   {  return returnValue;  }
}


// an algorithm can either do somehing, or also return value(s)
class Algorithm extends Statement implements Expression
{
   private String name;
   private String comment;	//* Description of the algorithm. Unescaped text!
   private AlgorithmStart algStart;
   private Vector<String> formalParameters = new Vector<String>();
   private Object returnValue;
   private boolean hasPerformed = false;
   
   Algorithm()
   {
      name = "Unnamed Algorithm"; // TODO this should be a random name?
      algStart = new AlgorithmStart();
   }

   public String getName()
   {
      return name;
   }

   public void setName(String n)
   {
      name = n;
   }

   public String getComment()
   {
      return comment;
   }

   public void setComment(String c)
   {
      comment = c;
   }

   public void setBody(Statement s)
   {
      algStart.setBody(s);
   }

   public void addFormalParameter(String varName)
   {
      formalParameters.add(varName);
   }

   public void setFormalParameters(String[] params)
   {
      formalParameters = new Vector<String>();
      for(String p : params)
      {
	 formalParameters.add(p);
      }
   }

   public Vector<String> getFormalParameters()
   {
      return formalParameters;
   }

   public int getFormalParameterCount()
   {
      return formalParameters.size();
   }

   // after this method, returnValue will be null if no return value, or the appropriate return value.
   public void performAction()
   {
      returnValue = null;
      try
      {
	 try
	 {
	    algStart.performAction();
	 }
	 catch(NotEnoughArgumentsException nnae)
	 {
	    // This is needed because if the Algorithm block has no body, it is actually the
	    // AlgorithmStart object that throws the exception; here we hide that from other code.
	    if(nnae.getErrorLocation() == algStart)
	       throw new NotEnoughArgumentsException(nnae.getMessage(), this);
	    else
	       throw nnae;
	 }
      }
      catch(ReturnFromAlgorithm rfa)
      {
	 returnValue = rfa.getReturnValue();
      }
      hasPerformed = true;
   }

   public Object getValue()
   {
      // if below is not commented out then the alg only
      // works once.  the other attempts to run only give
      // the returned value, none of the side-effects
      // like printing are performed.
      //if(!hasPerformed)
      {
	 performAction();
      }
      return returnValue;
   }
}



class IfStatement extends Statement
{
   private Expression condition;
   private Statement trueSmt, falseSmt;

   public void setCondition(Expression e)
   {
      condition = e;
   }

   public void setTrueStatement(Statement s)
   {
      trueSmt = s;
   }

   public void setFalseStatement(Statement s)
   {
      falseSmt = s;
   }

   public void performAction()
   {
      if(condition == null || trueSmt == null || falseSmt == null)
	 throw new NotEnoughArgumentsException("Empty space in If statement.", this);

      Object val = condition.getValue();
      if(val instanceof Boolean)
      {
	 boolean v = (Boolean)val;
	 if(v)
	 {
	    trueSmt.performAction();
	 }
	 else
	 {
//	    if(falseSmt == null)
//	       throw new NotEnoughArgumentsException("False branch cannot be empty!", this);
	    falseSmt.performAction();
	 }
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}


class WhileStatement extends Statement
{
   private Expression condition;
   private Statement body;

   public void setCondition(Expression e)
   {
      condition = e;
   }
   
   public void setBody(Statement s)
   {
      body = s;
   }

   public void performAction()
   {
      if(condition == null || body == null)
	 throw new NotEnoughArgumentsException("Empty space in While statement.", this);

      while(true)
      {
	 if(Call.abortProgram)
	    throw new ExecutionAbortedException();

	 Object val = condition.getValue();
	 if(!(val instanceof Boolean))
	    throw new InvalidArgumentTypeException("Condition to While must evaluate to Boolean");
	 
	 boolean b = (Boolean)val;
	 if(b)
	 {
	    // do body and check again
	    body.performAction();
	 }
	 else
	 {
	    // stop the loop
	    break;
	 }
      }
   }
}

class RepeatStatement extends Statement
{
   private Expression interations;
   private Statement body;

   public void setIteration(Expression e)
   {
      interations = e;
   }
   
   public void setBody(Statement s)
   {
      body = s;
   }

   public void performAction()
   {
      if(interations == null || body == null)
         throw new NotEnoughArgumentsException("Empty space in Repeat statement.", this);

      while(true)
      {
         if(Call.abortProgram)
            throw new ExecutionAbortedException();

         Object val = interations.getValue();
         if(!(val instanceof Integer))
            throw new InvalidArgumentTypeException("Number of iterations must be instance of integers with positive numbers");
         if ((Integer)val<=0)
            throw new InvalidArgumentTypeException("Number of iterations must be instance of integers with positive numbers");
         int b = (Integer)val;
         while (b>0) {
            body.performAction();
            b--;
         }    
      }
   }
}

// Use this to call another.  It takes the actual parameters and sets them up as the called algorithm's formal parameters.
class Call extends Statement implements Expression
{
   // long running primitives should check this and throw ExecutionAbortedException if true
   public static volatile boolean abortProgram = false; // seems as good a place as any to put it.

   private String algName;
   private Vector<Expression> actualParams = new Vector<Expression>();
   private boolean hasPerformed = false;
   private Object savedResult = null;

   public void setAlgorithmName(String name)
   {
      algName = name;
   }

   public String getAlgorithmName()
   {
      return algName;
   }
   
   public void addActualParameter(Expression e)
   {
      actualParams.add(e);
   }

   public void setActualParameters(Expression[] exps)
   {
      actualParams = new Vector<Expression>();
      for(Expression e : exps)
      {
	 actualParams.add(e);
      }
   }

   public void performAction()
   {
      // plan:
      // look up the algorithm name in the library
      // eval the actual parameters
      // setup the formal params for the alg
      // run the alg
      // get any return values

      if(Call.abortProgram)
	 throw new ExecutionAbortedException();

      Algorithm alg = Library.getAlgorithm(algName);
      if(alg == null)
      {
	 // can happen in here if a call is made to an algorithm then the algorithm's name is changed
	 throw new UndefinedAlgorithmException("Cannot find algorithm named " + algName);
      }

      // check that the number of formal and actual params are the same
      Vector<String> formalPs = alg.getFormalParameters();
      if(actualParams.size() != formalPs.size())
      {
	 throw new NotEnoughArgumentsException();
      }

      // check that all formal params have names, and are different
      for(int i = 0; i < formalPs.size(); i++)
      {
	 String s = formalPs.elementAt(i);
	 if(s == null || s.equals("") || formalPs.lastIndexOf(s) != i)
	 {
	    throw new NotEnoughArgumentsException();
	 }
      }


      // eval these args before pushing the new stack frame
      Object[] actualVals = new Object[actualParams.size()];
      for(int i = 0; i < actualVals.length; i++)
      {
	 Expression e = actualParams.elementAt(i);
	 if(e == null)
	 {
	    throw new NotEnoughArgumentsException();
	 }
	 Object result = e.getValue();
	 actualVals[i] = result;
      }

      // setup formal params via a new stack frame
      VarStackFrame frame = new VarStackFrame(algName);
      VarStack.pushFrame(frame);
      Vector<String> formal = alg.getFormalParameters();
      if(formal.size() != actualParams.size())
      {
	 throw new RuntimeException("Formal and Actual parameters differ in number for " + algName
				    + " (" + formal.size() + " != " + actualParams.size() + ")");
      }
      for(int i = 0; i < formal.size(); i++)
      {
	 Var v = new Var(formal.elementAt(i));
	 v.setValue(actualVals[i]);
	 frame.addVariable(v);
      }

      // run algs
      Object result = alg.getValue();

      // store return values
      savedResult = result;

      VarStack.popFrame(savedResult);
   }

   public Object getValue()
   {
      if(!hasPerformed)		// FIXME future problems??
      {
	 performAction();
      }
      return savedResult;
   }

}

// when an algorithm object is created it is added to the algorithm library by it's name.
// an algorithm is created by creating an Algorithm object in the GUI.
// the algorithm is then directly manipulated without going through this library.
class Library
{
   private static Vector<Algorithm> lib = new Vector<Algorithm>(); // don't map by name because alg name can change

   public static void addAlgorithm(Algorithm alg)
   {
      lib.add(alg);
   }

   public static void removeAlgorithm(Algorithm alg)
   {
      lib.remove(alg);
   }

   public static Algorithm getAlgorithm(String name)
   {
      for(Algorithm a : lib)
      {
	 if(a.getName().equals(name))
	    return a;
      }
      return null;		// FIXME something better?
   }

   public static void clearLibrary()
   {
      lib = new Vector<Algorithm>();
   }
}

// contains all the stack frames for the current overall algorithm call (ie, program)
class VarStack
{
   private static ArrayDeque<VarStackFrame> stack = new ArrayDeque<VarStackFrame>();

   public static void pushFrame(VarStackFrame f)
   {
      stack.addFirst(f);
      fireStackPushed(f.getEntryName());
   }

   // returnValue is only here to pass the return value from the pop at the end of a method
   // to the RuntimeInspector
   public static VarStackFrame popFrame(Object returnValue)
   {
      VarStackFrame f = stack.removeFirst();
      fireStackPopped(returnValue);
      return f;
   }

   public static VarStackFrame getCurrentFrame()
   {
      return stack.element();
   }

   // methods to help with RuntimeInspector event firing
   private static Vector<RuntimeInspector> inspectors = null;
   public static void addRuntimeInspector(RuntimeInspector ri)
   {
      if(inspectors == null)
	 inspectors = new Vector<RuntimeInspector>();
      inspectors.add(ri);
   }
   public static void removeRuntimeInspector(RuntimeInspector ri)
   {
      if(inspectors == null)
	 return;
      inspectors.remove(ri);
      if(inspectors.isEmpty())
	 inspectors = null;
   }
   static void fireVariableAdded(String n)
   {
      for(RuntimeInspector ri : inspectors)
	 ri.variableAdded(n);
   }
   static void fireVariableValueChanged(String n)
   {
      for(RuntimeInspector ri : inspectors)
	 ri.variableValueChanged(n);
   }
   static void fireStackPushed(String n)
   {
      for(RuntimeInspector ri : inspectors)
	 ri.stackPushed(n);
   }
   static void fireStackPopped(Object o)
   {
      for(RuntimeInspector ri : inspectors)
	 ri.stackPopped(o);
   }
}

// Contains all the variables for a Call invocation.  Therefore all the formal params and variables
// currently in scope are on the top VarStackFrame on the VarStack.
// Note that there are no global values in the language so these local/param var frames are enough.
class VarStackFrame
{
   private Vector<Var> vars = new Vector<Var>();
   private final String entryName; // name given when creating stack frame, the entered methods name

   /** Create a new stack frame sue to entering the given method. */
   public VarStackFrame(String name)
   {
      entryName = name;
   }

   /** Get the name given when creating this stack frame; it will be the name of the method
       being entered. */
   public String getEntryName()
   {
      return entryName;
   }

   /** Return the value of the given variable within this stack frame. */
   public Object getValue(String varName)
   {
      for(Var v : vars)
      {
	 if(v.getName().equals(varName))
	    return v.getValue();
      }
      throw new UndefinedVariableException(varName);
   }

   /** Add the given variable to the stack frame.  Will fire variableAdded event to any
       RuntimeInspectors. */
   public void addVariable(Var v)
   {
      vars.add(v);
      VarStack.fireVariableAdded(v.getName());
   }

   /** Change the value of the given variable to newValue.  Will fire variableValueChanged
       event to any RuntimeInspectors. */
   public void changeValue(String varName, Object newValue)
   {
      for(Var v : vars)
      {
	 if(v.getName().equals(varName))
	 {
	    v.setValue(newValue);
	    VarStack.fireVariableValueChanged(varName);
	    return;
	 }
      }
      throw new UndefinedVariableException(varName);
   }
}
interface RuntimeInspector
{
   public void variableValueChanged(String varName);
   public void variableAdded(String varName);
//   public void variableRemoved(String varName);  will this ever be used?
   public void stackPushed(String methName);
   public void stackPopped(Object returnValue);
}
// FIXME!!!! VALUES ON THE STACK HAVE NO INDICATED TYPE!!!!!!
// used internally to store the actual value of a variable as the program runs
class Var
{
   private final String name;
   private Object value;

   Var(String n)
   {
      name = n;
   }

   public String getName()   { return name; }
   public Object getValue()  { return value; }
   // TODO is this correct?  This method should only be used directly when the variable
   // is not yet on the stack, otherwise use VarStackFrame.changeValue(varName, newValue).
   public void setValue(Object o) { value = o; }
}

// Used to access the value of a variable on the stack.  Always resolved in the current stack frame.
class VarRef implements Expression
{
   private String name;

   public void setName(String n)
   {
      name = n;
   }

   public String getName()
   {
      return name;
   }
   
   public Object getValue()
   {
      VarStackFrame frame = VarStack.getCurrentFrame();
      return frame.getValue(name);
   }
}


class VarAssignment extends Statement
{
   private String name;
   private Expression value;

   public void setName(String n)
   {
      name = n;
   }

   public String getName()
   {
      return name;
   }

   public void setValue(Expression e)
   {
      value = e;
   }

   public void performAction()
   {
      if(value == null)
	 throw new NotEnoughArgumentsException("Empty space in variable assignment.", this);

      VarStackFrame frame = VarStack.getCurrentFrame();
      frame.changeValue(name, value.getValue());
   }
}

// used to declare a new var on the stack.
// all newly declared vars must have an initialisation value.
// FIXME add code to stack to guard against duplicate names.
// FIXME think about null situations for var names/values.
class VarDeclaration extends Statement
{
   private String name;
   private Expression value;

   public void setName(String n)
   {
      name = n;
   }
   public String getName()
   {
      return name;
   }

   public void setValue(Expression e)
   {
      value = e;
   }

   public void performAction()
   {
      if(value == null)
	 throw new NotEnoughArgumentsException("Empty space in variable declaration", this);

      VarStackFrame frame = VarStack.getCurrentFrame();
      
      Var v = new Var(name);
      v.setValue(value.getValue());
      frame.addVariable(v);
   }


}


