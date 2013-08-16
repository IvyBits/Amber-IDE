import amber.data.state.IStateManager;
import amber.data.state.LazyState;
import amber.data.state.Scope;
import amber.data.state.State;
import amber.data.state.node.IState;
import amber.data.state.xml.XMLStateManager;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;

/**
 *
 * @author Tudor
 */
public class StatesTest extends TestCase {

    @State(Scope.PROJECT)
    public static int life = 42;

    @LazyState(scope = Scope.PROJECT, name = "TestMapValue")
    public static Map<String, String> test() {
        Map map = new HashMap<String, String>();
        map.put("a", "b");
        return map;
    }

    public StatesTest(String testName) {
        super(testName);
    }

    public void testEquality() throws Exception {
        IStateManager man = new XMLStateManager();
        man.registerStateOwner(getClass());

        assertTrue(((IState< Map<String, String>>) man.getState(Scope.PROJECT, "TestMapValue")).get().equals(test()));
        man.emitStates();
        man.unregisterStateOwner(getClass());

        // TestIntegerValue state should have been cleared
        assertNull(man.getState(Scope.PROJECT, "TestIntegerValue"));

        Object stateHolder = new Object() {
            @LazyState(scope = Scope.PROJECT, name = "NestedNonStaticValue")
            public boolean boolState() {
                return true;
            }
        };
        man.registerStateOwner(stateHolder);
        assertTrue((Boolean) man.getState(Scope.PROJECT, "NestedNonStaticValue").get());
        man.unregisterStateOwner(stateHolder);
        assertNull(man.getState(Scope.PROJECT, "NestedNonStaticValue"));
    }
}
