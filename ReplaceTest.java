import java.util.regex.*;

class ReplaceTest
{
   public static void main(String[] args)
   {
      String input = "a\"b";
      String regex = "\"";
      String replace = "\\\\\"";

      System.out.println("Input: " + input);
      System.out.println("Regex: " + regex);
      System.out.println("Replace: " + replace);

      System.out.println("Result: " + input.replaceAll(regex, replace));
   }
}
