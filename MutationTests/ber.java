public class TestMutation {
//    boolean testing = x > 5 && x < 4, testing2 = y < 4;

    public boolean barMutation() {
        //if (x > 5) {}                                     // -> Exception, single expression
        //if (!(x > 5)) {}                                  // -> Exception, single expression
        //if (x > 5 || y < 4) {}                            // -> Removes random expression
        //if (!(x > 5) || (y > 5 && z > 5)) {}              // -> Removes random expression

        //boolean test = true;                              // -> Exception, not a binary expression
        //boolean test = x > 5 && y > 5;                    // -> Removes random expression

        //int x = y > 5 ? 0 : 1;                            // -> Removes the ternary expression and resolves to true value
        //int x = x > 5 && y > 5 ? 0 : 1;                   // -> Removes random expression

        //for (int i=0; i<10; i++) {}                       // -> Exception, single expression
        //for (int i=0; i<10 && i!=2; i++) {}               // -> Removes random expression

        //while (x > 5) {}                                  // -> Exception, single expression
        //while (x > 5 && y > 5) {}                         // -> Removes random expression

        //return x > 5;                                     // -> Exception, single expression
        //return x > 5 && y < 5;                            // -> Removes random expression


//      boolean test = a > 5 && a < 5, test2 = b < 4 || b > 5;
//      boolean test = x == 5
//      newVar = g < 5 && g < 5;
//      while (x > 5 && y > 5) {}
//      boolean x = x == 5 ? true : false;
//      for(int i=0; i<10; i++) {}
//        return x == 10;
//        if (c > 1 && c < 1) {
//           if(d < 1 || d < 2) {
//                newVar = x == 5;
//          } else if (e < 1 || e < 2) {}
//        } else if(f < 1 && f < 1) {}
//
//
//
//        boolean test = true;
//        if(test) {}
//
//        boolean testIFLoopVariable = true;if (testIFLoopVariable) {}
//        while (true) {}
    }
}