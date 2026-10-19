package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.request.CreateDeliveryRequest;

import java.util.List;

public interface IDeliveryService {
    Delivery assign(CreateDeliveryRequest request);
    Delivery create(Delivery delivery);
    Delivery read(String deliveryId);
    Delivery update(Delivery delivery);
    boolean delete(String deliveryId);
    List<Delivery> getAll();
}
