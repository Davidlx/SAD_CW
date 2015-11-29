/* RawLists.java
** List support in the RAW language
** 20081205 scotek created
*/

package org.scotek.vpl;

import java.util.LinkedList;

class ListNil implements Expression
{
   LinkedList val = new LinkedList();

   public Object getValue()
   {
      return val;
   }

   public String toString()
   {
      return "[]";
   }
}

class ListCons implements Expression
{
   Expression valExp, lstExp;

   public void setValueExpression(Expression e)
   {
      valExp = e;
   }
   public void setListExpression(Expression e)
   {
      lstExp = e;
   }

   public Object getValue()
   {
      Object valVal = valExp.getValue();
      Object lstVal = lstExp.getValue();

      if(lstVal instanceof LinkedList)
      {
	 LinkedList lstLst = (LinkedList)lstVal;
	 // I think shallow copy is ok here.  Since lists are only refered
	 // to by the start of the list, as long as anything happening to front
	 // of list returns a new list, things will be ok.
	 LinkedList newList = (LinkedList)lstLst.clone();
	 newList.addFirst(valVal);
	 return newList;
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }

   public String toString()
   {
      LinkedList res = (LinkedList)getValue();
      StringBuffer buf = new StringBuffer();

      buf.append("[");
      for(int i = 0; i < res.size() - 1; i++)
      {
	 buf.append(" ");
	 buf.append(res.get(i));
	 buf.append(",");
      }
      buf.append(" ");
      buf.append(res.getLast());
      buf.append("]");
      return buf.toString();
   }
}

class ListIsEmpty implements Expression
{
   Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      Object val = exp.getValue();

      if(val instanceof LinkedList)
      {
	 LinkedList ls = (LinkedList)val;
	 return ls.isEmpty();
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

class ListValue implements Expression
{
   Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      Object val = exp.getValue();

      if(val instanceof LinkedList)
      {
	 LinkedList ls = (LinkedList)val;
	 if(ls.size() == 0)
	 {
	    throw new InvalidArgumentTypeException("The parameter of value cannot be an empty list", this); // empty list
	 }
	 return ls.getFirst();
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}

class ListTail implements Expression
{
   Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      Object val = exp.getValue();

      if(val instanceof LinkedList)
      {
	 LinkedList ls = (LinkedList)val;
	 if(ls.size() == 0)
	 {
	    throw new InvalidArgumentTypeException("Cannot use the Empty List as an argument to Tail", this);	//EEH, this); // empty list 
	 }
	 else if(ls.size() == 1)
	 {
//sally list bug	    return new ListNil();
	    return new LinkedList();
	 }
	 else
	 {
	    LinkedList newList = (LinkedList)ls.clone();
	    newList.removeFirst();
	    return newList;
	 }
      }
      else
      {
	 throw new InvalidArgumentTypeException();
      }
   }
}
