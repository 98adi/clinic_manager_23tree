public class RatuvNode {
    public RatuvNode left;
    public RatuvNode middle;
    public RatuvNode right;
    public RatuvNode parent;
    public Object key;
    public int size;
    public long sum;

    public RatuvNode(Object key){
        this.key = key;
        this.size = 0;
        this.sum = 0;
    }
    public boolean isLeaf(){
        return left == null;
    }
    public void updateState(RatuvKeyHandler handler){
        if (isLeaf()){
            if (key == handler.getMaxKey()||key == handler.getMinKey()){
                this.size = 0;
                this.sum = 0;
            }
            else{
                this.size = 1;
                this.sum = handler.getLoad(key);
            }
        }
        else{
            this.size = 0;
            this.sum = 0;
            this.key = handler.getMinKey();

            if(left != null){
                this.size += left.size;
                this.sum += left.sum;
                this.key = left.key;
            }
            if(middle != null){
                this.size += middle.size;
                this.sum += middle.sum;
                this.key = middle.key;
            }
            if(right != null){
                this.size += right.size;
                this.sum += right.sum;
                this.key = right.key;
            }
        }
    }
}