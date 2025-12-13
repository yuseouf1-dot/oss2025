package src;

import java.util.ArrayList;

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
