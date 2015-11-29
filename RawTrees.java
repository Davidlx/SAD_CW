/* RawTrees.java
** Tree support in the RAW language
** 20090906 scotek created
** 20091203 scotek re-written to have separate storage and RAW classes.  Old version had many bugs.
*/

package org.scotek.vpl;

class StorageLeaf
{
   public StorageLeaf clone()
   {
      return new StorageLeaf();
   }

   public String toString()
   {
      return "!";
   }
}
class StorageNode extends StorageLeaf
{
   private StorageLeaf left, right;
   private Object value;

   public StorageNode(StorageLeaf l, Object v, StorageLeaf r)
   {
      left = l;
      value = v;
      right = r;
   }

   public Object getStoredValue()
   {
      return value;
   }

   public StorageLeaf getStoredLeft()
   {
      return left;
   }

   public StorageLeaf getStoredRight()
   {
      return right;
   }

   public StorageNode clone()
   {
      return new StorageNode(left.clone(), value, right.clone());
   }

   public String toString()
   {
      return "<" + left + ">" + value + "<" + right + ">";
   }
}



class TreeLeaf implements Expression
{
   StorageLeaf val = new StorageLeaf();

   public Object getValue()
   {
      return val;
   }

   public String toString()
   {
      return "!";
   }
}

class TreeNode implements Expression
{
   Expression ltree, rtree;
   Expression value;

   public void setLeftExpression(Expression e)
   {
      ltree = e;
   }
   public void setRightExpression(Expression e)
   {
      rtree = e;
   }
   public void setValueExpression(Expression e)
   {
      value = e;
   }

   public Object getValue()
   {
      if(ltree == null || rtree == null || value == null)
	 throw new NotEnoughArgumentsException("TreeNode is missing arguments", this);

      Object leftVal = ltree.getValue();
      Object rightVal = rtree.getValue();

      if(leftVal instanceof StorageLeaf && rightVal instanceof StorageLeaf)
      {
	 // make a shallow copy of the subtrees and return the newly constructed tree.
	 Object valVal = value.getValue();
	 StorageLeaf leftSN = (StorageLeaf)leftVal;
	 StorageLeaf rightSN = (StorageLeaf)rightVal;
	 return new StorageNode(leftSN.clone(), valVal, rightSN.clone());
      }
      else
      {
	 throw new InvalidArgumentTypeException("The left and right subtree values of a Node must be Nodes or Leafs!", this);
      }
   }

   public String toString()
   {
      return getValue().toString();
   }
}


class TreeIsLeaf implements Expression
{
   Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      if(exp == null)
	 throw new NotEnoughArgumentsException("TreeLeaf is missing arguments", this);

      Object val = exp.getValue();
      if(val instanceof StorageLeaf)
      {
	 if(val instanceof StorageNode)
	    return false;
	 else
	    return true;
      }
      else
	 return new InvalidArgumentTypeException("Argument to IsLeaf must be a tree", this);
   }
}

class TreeValue implements Expression
{
   Expression exp;
   
   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      if(exp == null)
	 throw new NotEnoughArgumentsException("TreeValue is missing arguments", this);

      Object val = exp.getValue();

      if(val instanceof StorageLeaf)
      {
	 if(val instanceof StorageNode)
	 {
	    StorageNode sn = (StorageNode)val;
	    return sn.getStoredValue();
	 }
	 else
	    throw new InvalidArgumentTypeException("Argument to Value cannot be a TreeLeaf", this);
      }
      else
	 throw new InvalidArgumentTypeException("Argument to Value must be a non-empty tree", this);
   }
}


class TreeLeft implements Expression
{
   Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      if(exp == null)
	 throw new NotEnoughArgumentsException("TreeLeft is missing arguments", this);

      Object val = exp.getValue();
      
      if(val instanceof StorageLeaf)
      {
	 if(val instanceof StorageNode)
	 {
	    StorageNode sn = (StorageNode)val;
	    return sn.getStoredLeft();
	 }
	 else
	    throw new InvalidArgumentTypeException("Argument to Left cannot be a TreeLeaf", this);  
      }
      else
	 throw new InvalidArgumentTypeException("Argument to Left must be a non-empty tree", this);
   }
}

class TreeRight implements Expression
{
   Expression exp;

   public void setExpression(Expression e)
   {
      exp = e;
   }

   public Object getValue()
   {
      if(exp == null)
	 throw new NotEnoughArgumentsException("TreeRight is missing arguments", this);
      
      Object val = exp.getValue();
      
      if(val instanceof StorageLeaf)
      {
	 if(val instanceof StorageNode)
	 {
	    StorageNode sn = (StorageNode)val;
	    return sn.getStoredRight();
	 }
	 else
	    throw new InvalidArgumentTypeException("Argument to Right cannot be a TreeLeaf", this);  
      }
      else
	 throw new InvalidArgumentTypeException("Argument to Right must be a non-empty tree", this); 
   }
}
