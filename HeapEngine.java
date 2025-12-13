import java.util.PriorityQueue;

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

