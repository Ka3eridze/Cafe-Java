public class OrderItem
{
    private int menuId;
    private int quantity;

    public OrderItem(int menuId, int quantity)
    {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public int getMenuId() { return menuId; }
    public int getQuantity() { return quantity; }
}
