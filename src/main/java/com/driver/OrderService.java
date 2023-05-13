package com.driver;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
    }

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Boolean addOrder(Order order) {

        Optional<Order> optOrder = getOrder(order.getId());
        if(optOrder.isPresent()) return true;
        orderRepository.addOrder(order);
        return true;
    }

    public Optional<Order> getOrder(String id) {

        Optional<Order> optionalOrder = orderRepository.getOrder(id);
        if(optionalOrder.isPresent()) return optionalOrder;
        return Optional.empty();
    }

    public Boolean addPartner(String partnerId) {

        Optional<DeliveryPartner> optionalDeliveryPartner = getPartner(partnerId);
        if(optionalDeliveryPartner.isPresent()) return true;
        orderRepository.addDeliveryPartner(new DeliveryPartner(partnerId));
        return true;
    }

    public Optional<DeliveryPartner> getPartner(String partnerId) {

        Optional<DeliveryPartner> optionalDeliveryPartner = orderRepository.getDeliveryPartner(partnerId);
        return  optionalDeliveryPartner;
    }

    public boolean addOrderPartnerPair(String orderId, String partnerId) {

        Optional<Order> optionalOrder = getOrder(orderId);
        Optional<DeliveryPartner> optionalDeliveryPartner = getPartner(partnerId);

        if(optionalOrder.isPresent() && optionalDeliveryPartner.isPresent()){
            orderRepository.addOrderPartnerPair(orderId,partnerId);
            optionalDeliveryPartner.get().setNumberOfOrders(optionalDeliveryPartner.get().getNumberOfOrders()+1);
        }
        return true;
    }

    public Optional<Integer> getOrderCountByPartnerId(String partnerId) {

        Optional<DeliveryPartner> optionalDeliveryPartner = orderRepository.getDeliveryPartner(partnerId);
        if(optionalDeliveryPartner.isEmpty()) return Optional.empty();

//        Optional<Integer> optionalCount = orderRepository.getOrderCountByPartnerId(partnerId);
        Optional<List<String>> optionalOrders = getOrdersByPartnerId(partnerId);
        int orderCt = 0;
        if(optionalOrders.isPresent()) orderCt = optionalOrders.get().size();
        return Optional.of(orderCt);
    }

    public Optional<List<String>> getOrdersByPartnerId(String partnerId) {

        Optional<List<String>> optionalOrders = orderRepository.getOrdersByPartnerId(partnerId);
        return optionalOrders;
    }

    public Optional<List<String>> getAllOrders() {

        return orderRepository.getAllOrders();
    }

    public Optional<Integer> getCountOfUnassignedOrders() {

        return Optional.of(orderRepository.getAllOrders().get().size() - orderRepository.getAssignedOrders().get().size());
    }

    public Optional<Integer> getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        Optional<List<String>> optionalOrders = orderRepository.getOrdersByPartnerId(partnerId);
        if(optionalOrders.isEmpty()) return Optional.of(0);
        int currTime = Order.convertDeliveryTime(time);
        List<String> orderIds = optionalOrders.get();
        Integer ct=0;
        for(String id : orderIds){

            int delTime = getOrder(id).get().getDeliveryTime();
            if(delTime>currTime)
                ct++;
        }

        return Optional.of(ct);
    }

    public Optional<String> getLastDeliveryTimeByPartnerId(String partnerId) {

        Optional<List<String>> optionalOrders = orderRepository.getOrdersByPartnerId(partnerId);
        if(optionalOrders.isEmpty()) return Optional.of("");
        List<String> orderIds = optionalOrders.get();
        int max = 0;
        for(String id : orderIds){

            int ctime = getOrder(id).get().getDeliveryTime();
            max = Math.max(ctime,max);
        }

        return Optional.of(Order.convertDeliveryTime(max));
    }

    public Boolean deletePartnerById(String partnerId) {

        Optional<List<String>> orders = getOrdersByPartnerId(partnerId);

        orderRepository.deletePartner(partnerId);

        if(orders.isEmpty()) return true;
        List<String> allOrders = orders.get();
        for(String orderId : allOrders){
        orderRepository.removeOrderPartnerMapping(orderId);
        }
        return true;
    }

    public Boolean deleteOrderById(String orderId) {

        String partnerId = orderRepository.getPartnerForOrder(orderId);
        orderRepository.deleteOrder(orderId);

        if (Objects.nonNull(partnerId)){
            orderRepository.removeOrderForPartner(partnerId,orderId);
            Optional<DeliveryPartner> optionalDeliveryPartner = getPartner(partnerId);
            if(optionalDeliveryPartner.isPresent()){
                int numOrders = optionalDeliveryPartner.get().getNumberOfOrders();
                numOrders--;
                if(numOrders<0) numOrders=0;
                optionalDeliveryPartner.get().setNumberOfOrders(numOrders);
            }
        }

        return true;
    }
}
