package com.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;
import java.util.UUID;

//Sistema de envios de pedidos utilizando rabbitmq.

public class Producer {

    private final static String QUEUE_NAME = "ordenesProductos";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            Scanner scanner = new Scanner(System.in);
            String continueSending = "y";

            while (continueSending.equalsIgnoreCase("y")) {
                System.out.println("Inserte el nombre del producto:");
                String producto = scanner.nextLine();

                System.out.println("Inserte la cantidad:");
                int cantidad = Integer.parseInt(scanner.nextLine());

                System.out.println("Inserte el precio por unidad:");
                double precio = Double.parseDouble(scanner.nextLine());

                System.out.println("Inserte el saldo actual del Usuario:");
                double saldo = Double.parseDouble(scanner.nextLine());
                
                precio = precio * cantidad;

                String ordenId = UUID.randomUUID().toString();
                String orden = "ID: " + ordenId + ", Producto: " + producto + ", Cantidad: " + cantidad + ", Precio por unidad: $" + precio + ", saldo: $" + saldo;

                channel.basicPublish("", QUEUE_NAME, null, orden.getBytes());
                System.out.println(" [x] Enviado: '" + orden + "'");

                System.out.println("¿Desea enviar otra orden? (y/n):");
                continueSending = scanner.nextLine();
            }
        }
    }
}

