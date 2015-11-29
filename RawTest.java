package org.scotek.vpl;

import java.util.Arrays;

class RawTest
{
   public static void main(String[] argv)
   {
      // expression and statement tests
//      printLiteralNumberTest();
//      printLiteralStringTest();
//      printLiteralBooleanTest();
//      sequentialPrintTest();
//      printIntegerExpressionTest();
//      printComplexIntegerExpressionTest();
//      printBooleanExpressionTest();
//      printComplexBooleanExpressionTest();
//      printEqualComparisonTest();
//      printComplexEqualComparisonTest();
//      printIfTest();

      // tests on algs returning values
//      returnTest();           WILL NO LONGER WORK, DESIGN CHANGE
//      multipleReturnTest();   WILL NO LONGER WORK, DESIGN CHANGE
//      algorithmTest();
//      multiReturnAlgorithmTest();
//      checkReturnStopsAlgorithmTest();

      // tests on the alg calling system
//      callTest();
//      callWithReturnTest();
//      usingCallResultValueTest();

      // now tests that actual use the alg parameter system
//      passthroughActualParamTest();
//      multiplyParameterTest();
//      squareParameterTest();
      // call using actual parameter as parameter
      // SKIPPED SO I CAN GET ON TO FACTORIAL, YAY!
//      factorialTest();

      // list tests
//      nilIsNil();
//      valueIsntNil();
      printList();
   }

   // expected result: 42
   static void printLiteralNumberTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      PrintStatement ps = new PrintStatement();
      LiteralInteger lit42 = new LiteralInteger(42);
      ps.setExpression(lit42);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // expected result: Hello World!
   static void printLiteralStringTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      PrintStatement ps = new PrintStatement();
      LiteralString lit = new LiteralString("Hello World!");
      ps.setExpression(lit);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // expected result: true
   static void printLiteralBooleanTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      PrintStatement ps = new PrintStatement();
      LiteralBoolean lit = new LiteralBoolean(true);
      ps.setExpression(lit);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // expected result: the four lines of the address
   static void sequentialPrintTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      PrintStatement ps = new PrintStatement();
      ps.setExpression(new LiteralString("The University of Nottingham Ningbo"));
      sseq.addStatement(ps);

      ps = new PrintStatement();
      ps.setExpression(new LiteralString("199 Taikang Dong Lu"));
      sseq.addStatement(ps);

      ps = new PrintStatement();
      ps.setExpression(new LiteralString("Ningbo"));
      sseq.addStatement(ps);

      ps = new PrintStatement();
      ps.setExpression(new LiteralInteger(31500));
      sseq.addStatement(ps);

      start.setBody(sseq);

      start.performAction();
   }

   // expected result: 17
   static void printNumberExpressionTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      AdditionOperator op = new AdditionOperator();
      op.setLeftOperand(new LiteralInteger(6));
      op.setRightOperand(new LiteralInteger(11));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(op);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // calculation: ((5*6) / (3+2)) - 2
   // expected result: 4
   static void printComplexNumberExpressionTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      MultiplicationOperator mulOp = new MultiplicationOperator();
      mulOp.setLeftOperand(new LiteralInteger(5));
      mulOp.setRightOperand(new LiteralInteger(6));

      AdditionOperator addOp = new AdditionOperator();
      addOp.setLeftOperand(new LiteralInteger(3));
      addOp.setRightOperand(new LiteralInteger(2));

      DivisionOperator divOp = new DivisionOperator();
      divOp.setLeftOperand(mulOp);
      divOp.setRightOperand(addOp);

      SubtractionOperator subOp = new SubtractionOperator();
      subOp.setLeftOperand(divOp);
      subOp.setRightOperand(new LiteralInteger(2));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(subOp);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // expected result: false
   static void printBooleanExpressionTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      AndOperator op = new AndOperator();
      op.setLeftOperand(new LiteralBoolean(true));
      op.setRightOperand(new LiteralBoolean(false));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(op);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // calculation: (false || true) && !false
   // expected result: true
   static void printComplexBooleanExpressionTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      OrOperator orOp = new OrOperator();
      orOp.setLeftOperand(new LiteralBoolean(false));
      orOp.setRightOperand(new LiteralBoolean(true));

      NotOperator notOp = new NotOperator();
      notOp.setOperand(new LiteralBoolean(false));

      AndOperator andOp = new AndOperator();
      andOp.setLeftOperand(orOp);
      andOp.setRightOperand(notOp);

      PrintStatement ps = new PrintStatement();
      ps.setExpression(andOp);

      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }

   // expected result: false, true
   static void printEqualComparisonTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      EqualsOperator op = new EqualsOperator();
      op.setLeftOperand(new LiteralBoolean(true));
      op.setRightOperand(new LiteralBoolean(false));
      PrintStatement ps = new PrintStatement();
      ps.setExpression(op);
      sseq.addStatement(ps);

      op = new EqualsOperator();
      op.setLeftOperand(new LiteralInteger(5));
      op.setRightOperand(new LiteralInteger(5));
      ps = new PrintStatement();
      ps.setExpression(op);
      sseq.addStatement(ps);

      start.setBody(sseq);

      start.performAction();
   }

   // calculation: ((6 + 9) > 3) == !false
   // expected result: true
   static void printComplexEqualComparisonTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      EqualsOperator op = makeEqualsOp(makeGreaterOp(makeAddOp(new LiteralInteger(6), new LiteralInteger(9)),
						     new LiteralInteger(3)),
				       makeNotOp(new LiteralBoolean(false)));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(op);
      sseq.addStatement(ps);
      start.setBody(sseq);

      start.performAction();
   }


   // expected result: 6 IS greater than 5!!!!
   static void printIfTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      PrintStatement truePs = new PrintStatement();
      truePs.setExpression(new LiteralString("6 IS greater than 5!!!!"));

      PrintStatement falsePs = new PrintStatement();
      falsePs.setExpression(new LiteralString("6 IS NOT greater than 5????"));

      IfStatement ifSt = new IfStatement();
      ifSt.setCondition(makeGreaterOp(new LiteralInteger(6), new LiteralInteger(5)));
      ifSt.setTrueStatement(truePs);
      ifSt.setFalseStatement(falsePs);

      sseq.addStatement(ifSt);

      start.setBody(sseq);

      start.performAction();
   }

/*   // This is the first thing to use AlgorithmEnd and return values
   // expected result: 327
   static void returnTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      start.setBody(sseq);

      AlgorithmEnd end = new AlgorithmEnd();
      end.addReturnValue(new LiteralInteger(327));

      // note that the next two steps aren't linked together here are they would be in the Algorithm object
      start.performAction();
      end.performAction();

      Object[] results = end.getEvaluatedValues();
      System.out.println(Arrays.toString(results));
   }

   // expected result: 327, true
   static void multipleReturnTest()
   {
      AlgorithmStart start = new AlgorithmStart();

      StatementSequence sseq = new StatementSequence();

      start.setBody(sseq);

      AlgorithmEnd end = new AlgorithmEnd();
      end.addReturnValue(new LiteralInteger(327));
      end.addReturnValue(makeEqualsOp(new LiteralBoolean(false), new LiteralBoolean(false)));

      // note that the next two steps aren't linked together here are they would be in the Algorithm object
      start.performAction();
      end.performAction();

      Object[] results = end.getEvaluatedValues();
      System.out.println(Arrays.toString(results));
   }
*/
   // expected result: 327
/*   static void algorithmTest()
   {
      Algorithm alg = new Algorithm();
      alg.setName("19");

      alg.addReturnValue(new LiteralInteger(327));
      alg.addReturnValue(makeEqualsOp(new LiteralBoolean(false), new LiteralBoolean(false)));

      Object result = alg.getValue();
      System.out.println(result);
   }
*/

   // expected result: 327
   static void algorithmTest()
   {
      Algorithm alg = new Algorithm();
      alg.setName("19");
      
      ReturnStatement ret = new ReturnStatement();
      ret.setReturnValue(new LiteralInteger(327));

      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ret);

      alg.setBody(sseq);

      Object result = alg.getValue();
      System.out.println(result);
   }
   
   // expected result: 456
   static void multiReturnAlgorithmTest()
   {
      Algorithm alg = new Algorithm();
      alg.setName("19");
      
      ReturnStatement retA = new ReturnStatement();
      retA.setReturnValue(new LiteralInteger(456));

      ReturnStatement retB = new ReturnStatement();
      retB.setReturnValue(new LiteralInteger(327));

      IfStatement ifSt = new IfStatement();
      ifSt.setCondition(new LiteralBoolean(true));
      ifSt.setTrueStatement(retA);
      ifSt.setFalseStatement(retB);

      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ifSt);

      alg.setBody(sseq);

      Object result = alg.getValue();
      System.out.println(result);
   }
   
   // expected result: 456
   static void checkReturnStopsAlgorithmTest()
   {
      Algorithm alg = new Algorithm();
      alg.setName("19");
      
      ReturnStatement retA = new ReturnStatement();
      retA.setReturnValue(new LiteralInteger(456));

      ReturnStatement retB = new ReturnStatement();
      retB.setReturnValue(new LiteralInteger(327));

      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(retA);
      sseq.addStatement(retB);

      alg.setBody(sseq);

      Object result = alg.getValue();
      System.out.println(result);
   }

   // expected result: 46, null
   static void callTest()
   {
      Algorithm alg = new Algorithm();
      alg.setName("Fixed Multiply");
      Library.addAlgorithm(alg);

      MultiplicationOperator mulOp = new MultiplicationOperator();
      mulOp.setLeftOperand(new LiteralInteger(23));
      mulOp.setRightOperand(new LiteralInteger(2));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(mulOp);
      
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps);

      alg.setBody(sseq);

      
      Call kall = new Call();
      kall.setAlgorithmName("Fixed Multiply");
      Object result = kall.getValue();

      System.out.println(result);
   }

   // expected result: 46, 7
   static void callWithReturnTest()
   {
      Algorithm alg = new Algorithm();
      alg.setName("Fixed Multiply");
      Library.addAlgorithm(alg);

      MultiplicationOperator mulOp = new MultiplicationOperator();
      mulOp.setLeftOperand(new LiteralInteger(23));
      mulOp.setRightOperand(new LiteralInteger(2));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(mulOp);
      
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps);

      ReturnStatement ret = new ReturnStatement();
      ret.setReturnValue(new LiteralInteger(7));
      sseq.addStatement(ret);

      alg.setBody(sseq);

      
      Call kall = new Call();
      kall.setAlgorithmName("Fixed Multiply");
      Object result = kall.getValue();

      System.out.println(result);
   }

   // In this test 2 algorithms are created.  The first always returns 7.
   // The second calls the first then multiplies the result by 2 and returns it.
   // expected result: 14
   static void usingCallResultValueTest()
   {
      // First alg to always return 7
      Algorithm alg = new Algorithm();
      alg.setName("Always return 7");
      Library.addAlgorithm(alg);
     
      ReturnStatement ret = new ReturnStatement();
      ret.setReturnValue(new LiteralInteger(7));
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ret);

      alg.setBody(sseq);

      // Second alg to call above alg and multiply return by 2
      Algorithm alg2 = new Algorithm();
      alg2.setName("Double answer of always return 7");
      Library.addAlgorithm(alg2);

      Call k = new Call();
      k.setAlgorithmName("Always return 7");

      MultiplicationOperator mulOp = new MultiplicationOperator();
      mulOp.setLeftOperand(k);
      mulOp.setRightOperand(new LiteralInteger(2));
      ReturnStatement ret2 = new ReturnStatement();
      ret2.setReturnValue(mulOp);
      StatementSequence sseq2 = new StatementSequence();
      sseq2.addStatement(ret2);
      
      alg2.setBody(sseq2);

      // now we actuall call the 2nd algorithm
      Call kall = new Call();
      kall.setAlgorithmName("Double answer of always return 7");
      Object result = kall.getValue();

      System.out.println(result);
   }

   // 2 algs, one of which takes and value and returns the same value back.
   // expected result: 9, null
   static void passthroughActualParamTest()
   {
      // set up passthorugh alg first
      Algorithm passAlg = new Algorithm();
      passAlg.setName("Passthrough");
      passAlg.addFormalParameter("x");
      Library.addAlgorithm(passAlg);

      VarRef passRef = new VarRef();
      passRef.setName("x");
      ReturnStatement passRet = new ReturnStatement();
      passRet.setReturnValue(passRef);
      StatementSequence passSseq = new StatementSequence();
      passSseq.addStatement(passRet);

      passAlg.setBody(passSseq);

      // now set up the main alg that calls the 1st one with a value then prints the returned result
      Algorithm printAlg = new Algorithm();
      printAlg.setName("Print");
      Library.addAlgorithm(printAlg);

      Call k = new Call();
      k.setAlgorithmName("Passthrough");
      k.addActualParameter(new LiteralInteger(9));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(k);
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps);

      printAlg.setBody(sseq);
      
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("Print");
      Object result = kall.getValue();
      System.out.println(result);
   }

   // 2 algs, one of which takes and value and returns it's double
   // expected result: 18, null
   static void multiplyParameterTest()
   {
      // set up passthorugh alg first
      Algorithm passAlg = new Algorithm();
      passAlg.setName("Double");
      passAlg.addFormalParameter("x");
      Library.addAlgorithm(passAlg);

      VarRef passRef = new VarRef();
      passRef.setName("x");
      ReturnStatement passRet = new ReturnStatement();
      passRet.setReturnValue(makeMulOp(passRef, new LiteralInteger(2)));
      StatementSequence passSseq = new StatementSequence();
      passSseq.addStatement(passRet);

      passAlg.setBody(passSseq);

      // now set up the main alg that calls the 1st one with a value then prints the returned result
      Algorithm printAlg = new Algorithm();
      printAlg.setName("Print");
      Library.addAlgorithm(printAlg);

      Call k = new Call();
      k.setAlgorithmName("Double");
      k.addActualParameter(new LiteralInteger(9));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(k);
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps);

      printAlg.setBody(sseq);
      
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("Print");
      Object result = kall.getValue();
      System.out.println(result);
   }

   // 2 algs, one of which takes and value and returns it's square.
   // More or less the same as last time, but uses the parameter multiple times
   // expected result: 81, null
   static void squareParameterTest()
   {
      // set up passthorugh alg first
      Algorithm passAlg = new Algorithm();
      passAlg.setName("Double");
      passAlg.addFormalParameter("x");
      Library.addAlgorithm(passAlg);

      VarRef passRef = new VarRef();
      passRef.setName("x");
      VarRef passRef2 = new VarRef();
      passRef2.setName("x");
      ReturnStatement passRet = new ReturnStatement();
      passRet.setReturnValue(makeMulOp(passRef, passRef2));
      StatementSequence passSseq = new StatementSequence();
      passSseq.addStatement(passRet);

      passAlg.setBody(passSseq);

      // now set up the main alg that calls the 1st one with a value then prints the returned result
      Algorithm printAlg = new Algorithm();
      printAlg.setName("Print");
      Library.addAlgorithm(printAlg);

      Call k = new Call();
      k.setAlgorithmName("Double");
      k.addActualParameter(new LiteralInteger(9));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(k);
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps);

      printAlg.setBody(sseq);
      
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("Print");
      Object result = kall.getValue();
      System.out.println(result);
   }

   // two args, the factorial alg and a main alg.
   // first test of recursion, and using parameters as further parameters.  Big jump from last test.
   // expected result: 120
   static void factorialTest()
   {
      // factorial algorithm
      Algorithm facAlg = new Algorithm();
      facAlg.setName("factorial");
      facAlg.addFormalParameter("n");
      Library.addAlgorithm(facAlg);

      VarRef ref = new VarRef();
      ref.setName("n");

      IfStatement ifSt = new IfStatement();
      ifSt.setCondition(makeEqualsOp(ref, new LiteralInteger(1)));
      ifSt.setTrueStatement(makeReturn(new LiteralInteger(1)));
      // recursive call
      Call k = new Call();
      k.setAlgorithmName("factorial");
      k.addActualParameter(makeSubOp(ref, new LiteralInteger(1)));
      ifSt.setFalseStatement(makeReturn(makeMulOp(ref, k)));

      StatementSequence facSseq = new StatementSequence();
      facSseq.addStatement(ifSt);

      facAlg.setBody(facSseq);
      
      // now set up the main alg that calls factorial and prints the returned result
      Algorithm printAlg = new Algorithm();
      printAlg.setName("Print");
      Library.addAlgorithm(printAlg);

      Call k2 = new Call();
      k2.setAlgorithmName("factorial");
      k2.addActualParameter(new LiteralInteger(5));

      PrintStatement ps = new PrintStatement();
      ps.setExpression(k2);
      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps);

      printAlg.setBody(sseq);
      
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("Print");
      Object result = kall.getValue();
      System.out.println(result);
   }


   static void nilIsNil()
   {
      Algorithm alg = new Algorithm();
      alg.setName("nilisnil");
      Library.addAlgorithm(alg);

      ListNil nil = new ListNil();

      ListIsEmpty isEmpty = new ListIsEmpty();
      isEmpty.setExpression(nil);
      
      PrintStatement ps = new PrintStatement();
      ps.setExpression(isEmpty);

      alg.setBody(ps);
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("nilisnil");
      Object result = kall.getValue();
      System.out.println(result);
   }

   static void valueIsntNil()
   {
      Algorithm alg = new Algorithm();
      alg.setName("valueisntnil");
      Library.addAlgorithm(alg);

      ListNil nil = new ListNil();
      ListCons cellOne = new ListCons();
      cellOne.setValueExpression(new LiteralInteger(39));
      cellOne.setListExpression(nil);

      ListIsEmpty isEmpty = new ListIsEmpty();
      isEmpty.setExpression(cellOne);
      
      PrintStatement ps = new PrintStatement();
      ps.setExpression(isEmpty);

      alg.setBody(ps);
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("valueisntnil");
      Object result = kall.getValue();
      System.out.println(result);
   }

   static void printList()
   {
      Algorithm alg = new Algorithm();
      alg.setName("printList");
      Library.addAlgorithm(alg);

      ListNil nil = new ListNil();
      ListCons cellOne = new ListCons();
      cellOne.setValueExpression(new LiteralInteger(39));
      cellOne.setListExpression(nil);
      ListCons cellTwo = new ListCons();
      cellTwo.setValueExpression(new LiteralInteger(38));
      cellTwo.setListExpression(cellOne);
      ListCons cellThree = new ListCons();
      cellThree.setValueExpression(new LiteralInteger(37));
      cellThree.setListExpression(cellTwo);

      PrintStatement ps1 = new PrintStatement();
      ps1.setExpression(cellThree);

      PrintStatement ps2 = new PrintStatement();
      ps2.setExpression(cellTwo);

      ListCons altOne = new ListCons();
      altOne.setValueExpression(new LiteralInteger(49));
      altOne.setListExpression(cellTwo);

      PrintStatement ps3 = new PrintStatement();
      ps3.setExpression(altOne);

      PrintStatement ps4 = new PrintStatement();
      ps4.setExpression(cellTwo);

      PrintStatement ps5 = new PrintStatement();
      ps5.setExpression(cellThree);

      StatementSequence sseq = new StatementSequence();
      sseq.addStatement(ps1);
      sseq.addStatement(ps2);
      sseq.addStatement(ps3);
      sseq.addStatement(ps4);
      sseq.addStatement(ps5);

      alg.setBody(sseq);
      // now call the main alg
      Call kall = new Call();
      kall.setAlgorithmName("printList");
      Object result = kall.getValue();
      System.out.println(result);
   }




   ///////////////////////////////////////////////////////////////////////////////////////////
   // some convenience methods for generating subexpressions more compactly
   static NotOperator makeNotOp(Expression e)
   {
      NotOperator op = new NotOperator();
      op.setOperand(e);
      return op;
   }
   static AdditionOperator makeAddOp(Expression l, Expression r)
   {
      AdditionOperator op = new AdditionOperator();
      op.setLeftOperand(l);
      op.setRightOperand(r);
      return op;
   }
   static SubtractionOperator makeSubOp(Expression l, Expression r)
   {
      SubtractionOperator op = new SubtractionOperator();
      op.setLeftOperand(l);
      op.setRightOperand(r);
      return op;
   }
   static MultiplicationOperator makeMulOp(Expression l, Expression r)
   {
      MultiplicationOperator op = new MultiplicationOperator();
      op.setLeftOperand(l);
      op.setRightOperand(r);
      return op;
   }
   static EqualsOperator makeEqualsOp(Expression l, Expression r)
   {
      EqualsOperator op = new EqualsOperator();
      op.setLeftOperand(l);
      op.setRightOperand(r);
      return op;
   }
   static GreaterOperator makeGreaterOp(Expression l, Expression r)
   {
      GreaterOperator op = new GreaterOperator();
      op.setLeftOperand(l);
      op.setRightOperand(r);
      return op;
   }
   static ReturnStatement makeReturn(Expression e)
   {
      ReturnStatement s = new ReturnStatement();
      s.setReturnValue(e);
      return s;
   }

}





