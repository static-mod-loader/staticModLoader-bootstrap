public class StaticLoaderProtocol {
    public interface CallbackTable {
        void test();
    }
    
    public native int GrabCallbackTable(CallbackTable table);
}