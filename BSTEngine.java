

class BSTEngine implements Engine {

    private class Node {
        Order order;
        Node left, right;

        Node(Order order) {
            this.order = order;
        }
    }

    Node buyRoot = null;
    Node sellRoot = null;

    private int compareOrders(Order o1, Order o2) {
        if (o1.price != o2.price) return o1.price - o2.price;
        return Long.compare(o1.timestamp, o2.timestamp);
    }

    private Node insertIterative(Node root, Order order) {
        Node newNode = new Node(order);
        
        if (root == null) {
            return newNode;
        }

        Node current = root;
        while (true) {
            if (compareOrders(order, current.order) < 0) {
                if (current.left == null) {
                    current.left = newNode;
                    break;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = newNode; 
                    break;
                }
                current = current.right;
            }
        }
        return root;
    }

    @Override
    public void addOrder(Order order) {
        if (order.type.equals("BUY")) {
            buyRoot = insertIterative(buyRoot, order);
        } else {
            sellRoot = insertIterative(sellRoot, order);
        }
    }

    private Order extractMin(Node[] rootRef) {
        Node root = rootRef[0];
        if (root == null) return null;

        Node parent = null;
        Node current = root;

        while (current.left != null) {
            parent = current;
            current = current.left;
        }

        if (parent == null) {
            rootRef[0] = current.right; 
        } else {
            parent.left = current.right;
        }
        return current.order;
    }

    private Order extractMax(Node[] rootRef) {
        Node root = rootRef[0];
        if (root == null) return null;

        Node parent = null;
        Node current = root;

        while (current.right != null) {
            parent = current;
            current = current.right;
        }

        if (parent == null) {
            rootRef[0] = current.left;
        } else {
            parent.right = current.left;
        }
        return current.order;
    }

    private Order peekMin(Node root) {
        if (root == null) return null;
        while (root.left != null) root = root.left;
        return root.order;
    }

    private Order peekMax(Node root) {
        if (root == null) return null;
        while (root.right != null) root = root.right;
        return root.order;
    }

    @Override
    public int matchOrders() {
        int matches = 0;
        while (buyRoot != null && sellRoot != null) {
            Order bestBuy = peekMax(buyRoot);
            Order bestSell = peekMin(sellRoot);

            if (bestBuy.price >= bestSell.price) {
                Node[] buyRef = { buyRoot };
                extractMax(buyRef);
                buyRoot = buyRef[0];

                Node[] sellRef = { sellRoot };
                extractMin(sellRef);
                sellRoot = sellRef[0];

                matches++;
            } else {
                break;
            }
        }
        return matches;
    }
}
