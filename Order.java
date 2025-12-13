
public class Order {
	int id;
    String type; 
    int price;
    long timestamp; 

    public Order(int id, String type, int price, long timestamp) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.timestamp = timestamp;
    }
}
