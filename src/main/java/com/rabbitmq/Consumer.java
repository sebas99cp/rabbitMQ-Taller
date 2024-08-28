package com.rabbitmq;

import com.rabbitmq.client.*;

public class Consumer {

    private final static String QUEUE_NAME = "ordenesProductos";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Esperando mensajes. presione CTRL+C para salir");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Recibido '" + message + "'");
            // Simular procesamiento del pedido
            ProcesarOrden(message);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    private static void ProcesarOrden(String order) {
        // Extraer el precio total y el saldo del pedido
        double precio = extractValue(order, "Precio por unidad: $");
        double saldo = extractValue(order, "saldo: $");

        try {
            Thread.sleep(3000); // Simula tiempo de procesamiento
            if (saldo >= precio) {
                System.out.println("Orden aprovada! Total: $" + precio + ", saldo: $" + saldo);
            } else {
                System.out.println("Orden rechazada, saldo insuficiente. Total: $" + precio + ", saldo: $" + saldo);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static double extractValue(String order, String key) {
        int start = order.indexOf(key) + key.length();
        int end = order.indexOf(",", start);
        if (end == -1) {
            end = order.length();
        }
        return Double.parseDouble(order.substring(start, end).trim());
    }
}
