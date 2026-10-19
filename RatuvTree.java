public class RatuvTree{
    private RatuvNode root;
    private final RatuvKeyHandler handler;

    public RatuvTree(RatuvKeyHandler handler){
        this.handler = handler;
        init();
    }
    private void init(){
        RatuvNode x = new RatuvNode(handler.getMaxKey());
        RatuvNode l = new RatuvNode(handler.getMinKey());
        RatuvNode m = new RatuvNode(handler.getMaxKey());
        x.left = l;
        x.middle = m;
        l.parent = x;
        m.parent = x;
        l.updateState(handler);
        m.updateState(handler);
        x.updateState(handler);
        this.root = x;
    }
    public Object search(Object k){
        return searchRec(root, k);
    }
    private Object searchRec(RatuvNode x, Object k){
        if (x.isLeaf()){
            if (handler.compare(x.key, k) == 0){
                return x.key;
            }
            return null;
        }
        if (handler.compare(k, x.left.key)<= 0){
            return searchRec(x.left, k);
        }
        else if (handler.compare(k, x.middle.key)<= 0){
            return searchRec(x.middle, k);
        }
        else{
            return searchRec(x.right, k);
        }
    }
    public void insert(Object zKey){
        RatuvNode z = new RatuvNode(zKey);
        z.updateState(handler);
        RatuvNode y = root;
        while (!y.isLeaf()){
            if (handler.compare(z.key, y.left.key)< 0){
                y = y.left;
            }
            else if (handler.compare(z.key, y.middle.key)< 0){
                y = y.middle;
            }
            else{
                y = y.right;
            }
        }
        RatuvNode x = y.parent;
        RatuvNode newChild = insertAndSplit(x, z);
        while (x != root){
            x = x.parent;
            if (newChild != null){
                newChild = insertAndSplit(x, newChild);
            }
            else{
                x.updateState(handler);
            }
        }
        if (newChild != null){
            RatuvNode s = new RatuvNode(handler.getMaxKey());
            setChildren(s, x, newChild, null);
            root = s;
            s.updateState(handler);
        }
    }
    private RatuvNode insertAndSplit(RatuvNode x, RatuvNode z){
        RatuvNode l = x.left;
        RatuvNode m = x.middle;
        RatuvNode r = x.right;
        if (r == null){
            if (handler.compare(z.key, l.key)< 0){
                setChildren(x, z, l, m);
            }
            else if (handler.compare(z.key, m.key)< 0){
                setChildren(x, l, z, m);
            }
            else{
                setChildren(x, l, m, z);
            }
            x.updateState(handler);
            return null;
        }
        RatuvNode y = new RatuvNode(handler.getMaxKey());
        if (handler.compare(z.key, l.key)< 0){
            setChildren(x, z, l, null);
            setChildren(y, m, r, null);
        }
        else if (handler.compare(z.key, m.key)< 0){
            setChildren(x, l, z, null);
            setChildren(y, m, r, null);
        }
        else if (handler.compare(z.key, r.key)< 0){
            setChildren(x, l, m, null);
            setChildren(y, z, r, null);
        }
        else{
            setChildren(x, l, m, null);
            setChildren(y, r, z, null);
        }
        x.updateState(handler);
        y.updateState(handler);
        return y;
    }
    private void setChildren(RatuvNode x, RatuvNode l, RatuvNode m, RatuvNode r){
        x.left = l;
        x.middle = m;
        x.right = r;
        if (l != null) l.parent = x;
        if (m != null) m.parent = x;
        if (r != null) r.parent = x;
    }
    public void delete(Object val){
        RatuvNode x = findLeaf(root, val);
        if (x == null) {
            return;
        }
        RatuvNode y = x.parent;
        if (x == y.left){
            setChildren(y, y.middle, y.right, null);
        }
        else if (x == y.middle){
            setChildren(y, y.left, y.right, null);
        }
        else{
            setChildren(y, y.left, y.middle, null);
        }
        while (y != null){
            y.updateState(handler);
            if (y.middle == null){
                if (y != root){
                    y = borrowOrMerge(y);
                }
                else{
                    root = y.left;
                    root.parent = null;
                    y = null;
                }
            }
            else{
                y = y.parent;
            }
        }
    }
    private RatuvNode findLeaf(RatuvNode x, Object k){
        if (x.isLeaf()){
            if (handler.compare(x.key, k) == 0){
                return x;
            }
            return null;
        }
        if (handler.compare(k, x.left.key)<= 0){
            return findLeaf(x.left, k);
        }
        else if (handler.compare(k, x.middle.key)<= 0){
            return findLeaf(x.middle, k);
        }
        else{
            return findLeaf(x.right, k);
        }
    }
    private RatuvNode borrowOrMerge(RatuvNode y){
        RatuvNode z = y.parent;
        RatuvNode x;
        if (y == z.left){
            x = z.middle;
            if (x.right != null){
                setChildren(y, y.left, x.left, null);
                setChildren(x, x.middle, x.right, null);
            }
            else{
                setChildren(x, y.left, x.left, x.middle);
                setChildren(z, x, z.right, null);
            }
        }
        else if(y == z.middle){
            x = z.left;
            if (x.right != null) {
                setChildren(y, x.right, y.left, null);
                setChildren(x, x.left, x.middle, null);
            }
            else{
                setChildren(x, x.left, x.middle, y.left);
                setChildren(z, x, z.right, null);
            }
        }
        else{
            x = z.middle;
            if (x.right != null){
                setChildren(y, x.right, y.left, null);
                setChildren(x, x.left, x.middle, null);
            }
            else{
                setChildren(x, x.left, x.middle, y.left);
                setChildren(z, z.left, x, null);
            }
        }
        y.updateState(handler);
        x.updateState(handler);
        z.updateState(handler);
        return z;
    }
    public int getRank(Object k){
        return rankRec(root, k);
    }
    private int rankRec(RatuvNode x, Object k){
        if (x.isLeaf()){
            if (handler.compare(x.key, k)<= 0 && x.size > 0){
                return 1;
            }
            return 0;
        }
        if (handler.compare(k, x.left.key)<= 0){
            return rankRec(x.left, k);
        }
        else if (handler.compare(k, x.middle.key)<= 0){
            return x.left.size + rankRec(x.middle, k);
        }
        else{
            return x.left.size + x.middle.size + rankRec(x.right, k);
        }
    }
    public long getSumLoad(Object k){
        return sumRec(root, k);
    }
    private long sumRec(RatuvNode x, Object k){
        if (x.isLeaf()){
            if (handler.compare(x.key, k)<= 0){
                return x.sum;
            }
            return 0;
        }
        if (handler.compare(k, x.left.key)<= 0){
            return sumRec(x.left, k);
        }
        else if (handler.compare(k, x.middle.key)<= 0){
            return x.left.sum + sumRec(x.middle, k);
        }
        else{
            return x.left.sum + x.middle.sum + sumRec(x.right, k);
        }
    }
}