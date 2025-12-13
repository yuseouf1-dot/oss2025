import java.util.*;




class HeapEngine implements Engine {
    PriorityQueue<Order> buyQueue;
    PriorityQueue<Order> sellQueue;

    public HeapEngine() {
        buyQueue = new PriorityQueue<>((o1, o2) -> {
            if (o2.price != o1.price) return o2.price - o1.price;
            return Long.compare(o1.timestamp, o2.timestamp);
        });

        sellQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.price != o2.price) return o1.price - o2.price;
            return Long.compare(o1.timestamp, o2.timestamp);
        });
    }

    @Override
    public void addOrder(Order order) {
        if (order.type.equals("BUY")) buyQueue.add(order);
        else sellQueue.add(order);
    }

    @Override
    public int matchOrders() {
        int matches = 0;
        while (!buyQueue.isEmpty() && !sellQueue.isEmpty()) {
            Order bestBuy = buyQueue.peek();
            Order bestSell = sellQueue.peek();

            if (bestBuy.price >= bestSell.price) {
                buyQueue.poll();
                sellQueue.poll();
                matches++;
            } else {
                break; 
            }
        }
        return matches;
    }
}


class ListEngine implements Engine {
    ArrayList<Order> buyList = new ArrayList<>();
    ArrayList<Order> sellList = new ArrayList<>();

    @Override
    public void addOrder(Order order) {
        // 추가는 빠름 O(1)
        if (order.type.equals("BUY")) buyList.add(order);
        else sellList.add(order);
    }

    @Override
    public int matchOrders() {
        int matches = 0;
        boolean matched;
        
        do {
            matched = false;
            int bestBuyIdx = -1;
            int bestSellIdx = -1;

            int maxPrice = -1;
            for (int i = 0; i < buyList.size(); i++) {
                Order o = buyList.get(i);
                if (bestBuyIdx == -1 || o.price > maxPrice) {
                    maxPrice = o.price;
                    bestBuyIdx = i;
                }
            }

            int minPrice = Integer.MAX_VALUE;
            for (int i = 0; i < sellList.size(); i++) {
                Order o = sellList.get(i);
                if (bestSellIdx == -1 || o.price < minPrice) {
                    minPrice = o.price;
                    bestSellIdx = i;
                }
            }


            if (bestBuyIdx != -1 && bestSellIdx != -1) {
                Order buy = buyList.get(bestBuyIdx);
                Order sell = sellList.get(bestSellIdx);

                if (buy.price >= sell.price) {
                    buyList.remove(bestBuyIdx);
                    sellList.remove(bestSellIdx);
                    matches++;
                    matched = true; 
                }
            }
        } while (matched); 

        return matches;
    }
}

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