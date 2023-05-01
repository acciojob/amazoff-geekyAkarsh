package com.driver;

import net.bytebuddy.dynamic.DynamicType;
import org.springframework.stereotype.Repository;

import java.awt.font.OpenType;
import java.util.*;

@Repository
public class OrderRepository {

    private Map<String,Order> orderMap = new HashMap<>();
    private Map<String,DeliveryPartner> deliveryPartnerMap = new HashMap<>();
    private Map<String,String> orderToPartnerMap = new HashMap<>();
    private Map<String, List<String>> partnerToOrderMap = new HashMap<>();

    public OrderRepository() {

        this.orderMap = new HashMap<>();
        this.deliveryPartnerMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
    }

    public void addOrder(Order order) {

        orderMap.put(order.getId(),order);
        return;
    }

    public Optional<Order> getOrder(String id){

        if(orderMap.containsKey(id)){
            return Optional.of(orderMap.get(id));
        }
        return Optional.empty();
    }

    public void addDeliveryPartner(DeliveryPartner deliveryPartner) {

        deliveryPartnerMap.put(deliveryPartner.getId(), deliveryPartner);
        return;
    }

    public Optional<DeliveryPartner> getDeliveryPartner(String partnerId) {

        if(deliveryPartnerMap.containsKey(partnerId)){
            return Optional.of(deliveryPartnerMap.get(partnerId));
        }
        return Optional.empty();
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        orderToPartnerMap.put(orderId,partnerId);
        List<String> oldOrders;
        if(partnerToOrderMap.containsKey(partnerId))
            oldOrders = partnerToOrderMap.get(partnerId);
        else
            oldOrders = new ArrayList<>();
        oldOrders.add(orderId);
        partnerToOrderMap.put(partnerId,oldOrders);
        return;
    }

    public Optional<Integer> getOrderCountByPartnerId(String partnerId) {

        if(partnerToOrderMap.containsKey(partnerId))
            return Optional.of(partnerToOrderMap.get(partnerId).size());

        return Optional.empty();
    }

    public Optional<List<String>> getOrdersByPartnerId(String partnerId) {

        if(partnerToOrderMap.containsKey(partnerId)){
            return Optional.of(partnerToOrderMap.get(partnerId));
        }

        return Optional.empty();
    }

    public Optional<List<String>> getAllOrders() {

        if(orderMap.size() == 0) return Optional.of(new ArrayList<>());
        return Optional.of(new ArrayList<>(orderMap.keySet()));
    }

    public boolean isAssigned(String order) {

        if(orderToPartnerMap.containsKey(order))
            return true;
        return false;
    }

    public Optional<List<String>> getAssignedOrders() {

        if(orderToPartnerMap.size() == 0) return Optional.of(new ArrayList<>());
        return Optional.of(new ArrayList<>(orderToPartnerMap.keySet()));
    }

    public void deletePartner(String partnerId) {

        deliveryPartnerMap.remove(partnerId);
        partnerToOrderMap.remove(partnerId);
        return;
    }

    public void removeOrderPartnerMapping(String id) {

        orderToPartnerMap.remove(id);
        return;
    }

    public void deleteOrder(String orderId) {

        orderMap.remove(orderId);
        orderToPartnerMap.remove(orderId);
        return;
    }

    public String getPartnerForOrder(String orderId) {

        return orderToPartnerMap.get(orderId);
    }

    public void removeOrderForPartner(String partnerId, String orderId) {

        List<String> orders = partnerToOrderMap.get(partnerId);
        orders.remove(orderId);
        partnerToOrderMap.put(partnerId,orders);
        return;
    }
}
