/**
 * 
 */


import java.util.HashMap;
import java.util.Map;

/**
 * @author Lunhao Liang <lunhaol@bu.edu>
 * @since Fall 2018
 */
public class TestCases extends CyclicIterator<Map<String, Angled>> {

  Map<String, Angled> stop() {
    return this.stop;
  }

  private final Map<String, Angled> stop;

  @SuppressWarnings("unchecked")
  TestCases() {
    this.stop = new HashMap<String, Angled>();
    final Map<String, Angled> first = new HashMap<String, Angled>();
    final Map<String, Angled> second = new HashMap<String, Angled>();
    final Map<String, Angled> third = new HashMap<String, Angled>();
    final Map<String, Angled> forth = new HashMap<String, Angled>();

    super.add(stop, first, second, third, forth);

    // the body, abdomen do not change through any of the
    // test cases
    // the stop test case
    stop.put(PA2.BODY_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.A_BODY_NAME, new BaseAngled(0, -130, 0));
    stop.put(PA2.A_INMIDDLE_NAME, new BaseAngled(15, -10, 0));
    stop.put(PA2.A_OUTMIDDLE_NAME, new BaseAngled(15, -10, 0));
    stop.put(PA2.A_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.B_BODY_NAME, new BaseAngled(0, -85, 0));
    stop.put(PA2.B_INMIDDLE_NAME, new BaseAngled(15, -55, 0));
    stop.put(PA2.B_OUTMIDDLE_NAME, new BaseAngled(15, -10, 0));
    stop.put(PA2.B_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.C_BODY_NAME, new BaseAngled(0, -85, 0));
    stop.put(PA2.C_INMIDDLE_NAME, new BaseAngled(15, -15, 0));
    stop.put(PA2.C_OUTMIDDLE_NAME, new BaseAngled(15, -30, 0));
    stop.put(PA2.C_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.D_BODY_NAME, new BaseAngled(0, -50, 0));
    stop.put(PA2.D_INMIDDLE_NAME, new BaseAngled(15, 10, 0));
    stop.put(PA2.D_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    stop.put(PA2.D_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.a_BODY_NAME, new BaseAngled(0, 130, 0));
    stop.put(PA2.a_INMIDDLE_NAME, new BaseAngled(15, 10, 0));
    stop.put(PA2.a_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    stop.put(PA2.a_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.b_BODY_NAME, new BaseAngled(0, 85, 0));
    stop.put(PA2.b_INMIDDLE_NAME, new BaseAngled(15, 55, 0));
    stop.put(PA2.b_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    stop.put(PA2.b_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.c_BODY_NAME, new BaseAngled(0, 85, 0));
    stop.put(PA2.c_INMIDDLE_NAME, new BaseAngled(15, 15, 0));
    stop.put(PA2.c_OUTMIDDLE_NAME, new BaseAngled(15, 30, 0));
    stop.put(PA2.c_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.d_BODY_NAME, new BaseAngled(0, 50, 0));
    stop.put(PA2.d_INMIDDLE_NAME, new BaseAngled(15, -10, 0));
    stop.put(PA2.d_OUTMIDDLE_NAME, new BaseAngled(15, -10, 0));
    stop.put(PA2.d_DISTAL_NAME, new BaseAngled(0, 0, 0));
    
    // the first test case
    first.put(PA2.BODY_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.A_BODY_NAME, new BaseAngled(0, -130, 0));
    first.put(PA2.A_INMIDDLE_NAME, new BaseAngled(40, -10, 0));
    first.put(PA2.A_OUTMIDDLE_NAME, new BaseAngled(40, -10, 0));
    first.put(PA2.A_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.B_BODY_NAME, new BaseAngled(0, -85, 0));
    first.put(PA2.B_INMIDDLE_NAME, new BaseAngled(40, -55, 0));
    first.put(PA2.B_OUTMIDDLE_NAME, new BaseAngled(40, -10, 0));
    first.put(PA2.B_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.C_BODY_NAME, new BaseAngled(0, -85, 0));
    first.put(PA2.C_INMIDDLE_NAME, new BaseAngled(40, -15, 0));
    first.put(PA2.C_OUTMIDDLE_NAME, new BaseAngled(40, -30, 0));
    first.put(PA2.C_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.D_BODY_NAME, new BaseAngled(0, -50, 0));
    first.put(PA2.D_INMIDDLE_NAME, new BaseAngled(40, 10, 0));
    first.put(PA2.D_OUTMIDDLE_NAME, new BaseAngled(40, 10, 0));
    first.put(PA2.D_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.a_BODY_NAME, new BaseAngled(0, 130, 0));
    first.put(PA2.a_INMIDDLE_NAME, new BaseAngled(40, 10, 0));
    first.put(PA2.a_OUTMIDDLE_NAME, new BaseAngled(40, 10, 0));
    first.put(PA2.a_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.b_BODY_NAME, new BaseAngled(0, 85, 0));
    first.put(PA2.b_INMIDDLE_NAME, new BaseAngled(40, 55, 0));
    first.put(PA2.b_OUTMIDDLE_NAME, new BaseAngled(40, 10, 0));
    first.put(PA2.b_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.c_BODY_NAME, new BaseAngled(0, 85, 0));
    first.put(PA2.c_INMIDDLE_NAME, new BaseAngled(40, 15, 0));
    first.put(PA2.c_OUTMIDDLE_NAME, new BaseAngled(40, 30, 0));
    first.put(PA2.c_DISTAL_NAME, new BaseAngled(0, 0, 0));
    first.put(PA2.d_BODY_NAME, new BaseAngled(0, 50, 0));
    first.put(PA2.d_INMIDDLE_NAME, new BaseAngled(40, -10, 0));
    first.put(PA2.d_OUTMIDDLE_NAME, new BaseAngled(40, -10, 0));
    first.put(PA2.d_DISTAL_NAME, new BaseAngled(0, 0, 0));
    
    // the second test case
    second.put(PA2.BODY_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.A_BODY_NAME, new BaseAngled(0, -150, 0));
    second.put(PA2.A_INMIDDLE_NAME, new BaseAngled(15, -30, 0));
    second.put(PA2.A_OUTMIDDLE_NAME, new BaseAngled(15, -30, 0));
    second.put(PA2.A_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.B_BODY_NAME, new BaseAngled(0, -105, 0));
    second.put(PA2.B_INMIDDLE_NAME, new BaseAngled(15, -65, 0));
    second.put(PA2.B_OUTMIDDLE_NAME, new BaseAngled(15, -10, 0));
    second.put(PA2.B_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.C_BODY_NAME, new BaseAngled(0, -105, 0));
    second.put(PA2.C_INMIDDLE_NAME, new BaseAngled(15, -25, 0));
    second.put(PA2.C_OUTMIDDLE_NAME, new BaseAngled(15, -40, 0));
    second.put(PA2.C_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.D_BODY_NAME, new BaseAngled(0, -70, 0));
    second.put(PA2.D_INMIDDLE_NAME, new BaseAngled(15, -30, 0));
    second.put(PA2.D_OUTMIDDLE_NAME, new BaseAngled(15, -30, 0));
    second.put(PA2.D_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.a_BODY_NAME, new BaseAngled(0, 110, 0));
    second.put(PA2.a_INMIDDLE_NAME, new BaseAngled(15, 0, 0));
    second.put(PA2.a_OUTMIDDLE_NAME, new BaseAngled(15, 0, 0));
    second.put(PA2.a_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.b_BODY_NAME, new BaseAngled(0, 65, 0));
    second.put(PA2.b_INMIDDLE_NAME, new BaseAngled(15, 15, 0));
    second.put(PA2.b_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    second.put(PA2.b_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.c_BODY_NAME, new BaseAngled(0, 65, 0));
    second.put(PA2.c_INMIDDLE_NAME, new BaseAngled(15, 5, 0));
    second.put(PA2.c_OUTMIDDLE_NAME, new BaseAngled(15, 20, 0));
    second.put(PA2.c_DISTAL_NAME, new BaseAngled(0, 0, 0));
    second.put(PA2.d_BODY_NAME, new BaseAngled(0, 30, 0));
    second.put(PA2.d_INMIDDLE_NAME, new BaseAngled(15, -10, 0));
    second.put(PA2.d_OUTMIDDLE_NAME, new BaseAngled(15, -10, 0));
    second.put(PA2.d_DISTAL_NAME, new BaseAngled(0, 0, 0));
    
    // the third test case
    third.put(PA2.BODY_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.A_BODY_NAME, new BaseAngled(0, -110, 0));
    third.put(PA2.A_INMIDDLE_NAME, new BaseAngled(15, 10, 0));
    third.put(PA2.A_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    third.put(PA2.A_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.B_BODY_NAME, new BaseAngled(0, -55, 0));
    third.put(PA2.B_INMIDDLE_NAME, new BaseAngled(15, -45, 0));
    third.put(PA2.B_OUTMIDDLE_NAME, new BaseAngled(15, -10, 0));
    third.put(PA2.B_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.C_BODY_NAME, new BaseAngled(0, -45, 0));
    third.put(PA2.C_INMIDDLE_NAME, new BaseAngled(15, -5, 0));
    third.put(PA2.C_OUTMIDDLE_NAME, new BaseAngled(15, -20, 0));
    third.put(PA2.C_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.D_BODY_NAME, new BaseAngled(0, -40, 0));
    third.put(PA2.D_INMIDDLE_NAME, new BaseAngled(15, 30, 0));
    third.put(PA2.D_OUTMIDDLE_NAME, new BaseAngled(15, 30, 0));
    third.put(PA2.D_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.a_BODY_NAME, new BaseAngled(0, 150, 0));
    third.put(PA2.a_INMIDDLE_NAME, new BaseAngled(15, 20, 0));
    third.put(PA2.a_OUTMIDDLE_NAME, new BaseAngled(15, 20, 0));
    third.put(PA2.a_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.b_BODY_NAME, new BaseAngled(0, 105, 0));
    third.put(PA2.b_INMIDDLE_NAME, new BaseAngled(15, 55, 0));
    third.put(PA2.b_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    third.put(PA2.b_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.c_BODY_NAME, new BaseAngled(0, 105, 0));
    third.put(PA2.c_INMIDDLE_NAME, new BaseAngled(15, 25, 0));
    third.put(PA2.c_OUTMIDDLE_NAME, new BaseAngled(15, 40, 0));
    third.put(PA2.c_DISTAL_NAME, new BaseAngled(0, 0, 0));
    third.put(PA2.d_BODY_NAME, new BaseAngled(0, 70, 0));
    third.put(PA2.d_INMIDDLE_NAME, new BaseAngled(15, 10, 0));
    third.put(PA2.d_OUTMIDDLE_NAME, new BaseAngled(15, 10, 0));
    third.put(PA2.d_DISTAL_NAME, new BaseAngled(0, 0, 0));
    
    // the forth test case
    forth.put(PA2.BODY_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.A_BODY_NAME, new BaseAngled(0, -130, 0));
    forth.put(PA2.A_INMIDDLE_NAME, new BaseAngled(40, -10, 0));
    forth.put(PA2.A_OUTMIDDLE_NAME, new BaseAngled(40, -10, 0));
    forth.put(PA2.A_DISTAL_NAME, new BaseAngled(0, 0, -10));
    forth.put(PA2.B_BODY_NAME, new BaseAngled(0, -85, 0));
    forth.put(PA2.B_INMIDDLE_NAME, new BaseAngled(40, -55, 0));
    forth.put(PA2.B_OUTMIDDLE_NAME, new BaseAngled(40, -10, 0));
    forth.put(PA2.B_DISTAL_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.C_BODY_NAME, new BaseAngled(0, -85, 0));
    forth.put(PA2.C_INMIDDLE_NAME, new BaseAngled(40, -15, 0));
    forth.put(PA2.C_OUTMIDDLE_NAME, new BaseAngled(40, -30, 0));
    forth.put(PA2.C_DISTAL_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.D_BODY_NAME, new BaseAngled(0, -50, 0));
    forth.put(PA2.D_INMIDDLE_NAME, new BaseAngled(40, 10, 0));
    forth.put(PA2.D_OUTMIDDLE_NAME, new BaseAngled(40, 10, 0));
    forth.put(PA2.D_DISTAL_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.a_BODY_NAME, new BaseAngled(0, 130, 0));
    forth.put(PA2.a_INMIDDLE_NAME, new BaseAngled(-40, 10, 0));
    forth.put(PA2.a_OUTMIDDLE_NAME, new BaseAngled(-40, 10, 0));
    forth.put(PA2.a_DISTAL_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.b_BODY_NAME, new BaseAngled(0, 85, 0));
    forth.put(PA2.b_INMIDDLE_NAME, new BaseAngled(-40, 55, 0));
    forth.put(PA2.b_OUTMIDDLE_NAME, new BaseAngled(-40, 10, 0));
    forth.put(PA2.b_DISTAL_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.c_BODY_NAME, new BaseAngled(0, 85, 0));
    forth.put(PA2.c_INMIDDLE_NAME, new BaseAngled(-40, 15, 0));
    forth.put(PA2.c_OUTMIDDLE_NAME, new BaseAngled(-40, 30, 0));
    forth.put(PA2.c_DISTAL_NAME, new BaseAngled(0, 0, 0));
    forth.put(PA2.d_BODY_NAME, new BaseAngled(0, 50, 0));
    forth.put(PA2.d_INMIDDLE_NAME, new BaseAngled(-40, -10, 0));
    forth.put(PA2.d_OUTMIDDLE_NAME, new BaseAngled(-40, -10, 0));
    forth.put(PA2.d_DISTAL_NAME, new BaseAngled(0, 0, 0));
  }
}
