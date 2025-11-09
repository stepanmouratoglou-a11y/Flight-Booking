import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ticket {

    private final Flight flight;
    private final String ticketNumber;
    private final double pricePerSeat;
    private final int seats;
    private final BaggagePackage baggagePackage;

    public enum BaggagePackage {
        NO_BAGGAGE("No checked baggage (cabin bag only)", 0.0),
        BASIC("1 checked bag up to 23kg", 30.0),
        PREMIUM("2 checked bags up to 23kg each", 50.0),
        BUSINESS("2 checked bags up to 32kg each + priority", 80.0);

        private final String description;
        private final double price;

        BaggagePackage(String description, double price) {
            this.description = description;
            this.price = price;
        }

        public String getDescription() { return description; }
        public double getPrice() { return price; }

        public static void printPackages() {
            System.out.println("\nAvailable Baggage Packages:");
            System.out.println("----------------------------------------");
            for (BaggagePackage pkg : values()) {
                System.out.printf("%d. %s - €%.2f%n", pkg.ordinal() + 1, pkg.description, pkg.price);
            }
        }

        public static BaggagePackage fromChoice(int choice) {
            if (choice < 1 || choice > values().length) {
                return NO_BAGGAGE; 
            }
            return values()[choice - 1];
        }
    }

    public Ticket(Flight flight, String ticketNumber, int seats, double pricePerSeat, BaggagePackage baggage){
        this.flight = flight;
        this.ticketNumber = ticketNumber;
        this.seats = seats;
        this.pricePerSeat = pricePerSeat;
        this.baggagePackage = baggage;
    }

    public double getPricePerSeat(){
        return pricePerSeat;
    }

    public int getSeats(){
        return seats;
    }

    public double getTotalPrice(){
        return (pricePerSeat + baggagePackage.getPrice()) * seats;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Flight getFlight() {
        return flight;
    }

    public BaggagePackage getBaggagePackage() {
        return baggagePackage;
    }

    @Override
    public String toString(){
        return flight.toString() + String.format(" | Ticket#: %s | Seats: %d | Baggage: %s | Price/seat: %.2f | Baggage fee: %.2f | Total: %.2f",
            ticketNumber == null ? "N/A" : ticketNumber,
            seats,
            baggagePackage.getDescription(),
            pricePerSeat,
            baggagePackage.getPrice(),
            getTotalPrice());
    }

    
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    private static final Map<String, Double> AIRLINE_RATES = new HashMap<>();

    static {
        CITY_COORDS.put("Athens", new double[]{37.9838, 23.7275});
        CITY_COORDS.put("London", new double[]{51.5074, -0.1278});
        CITY_COORDS.put("Paris", new double[]{48.8566, 2.3522});
        CITY_COORDS.put("Rome", new double[]{41.9028, 12.4964});
        CITY_COORDS.put("Berlin", new double[]{52.52, 13.4050});
        CITY_COORDS.put("New York", new double[]{40.7128, -74.0060});
        CITY_COORDS.put("Madrid", new double[]{40.4168, -3.7038});
        CITY_COORDS.put("Athina", new double[]{37.9838, 23.7275});

        
        AIRLINE_RATES.put("Emirates", 1.3); 
        AIRLINE_RATES.put("Ryanair", 0.7);  
        AIRLINE_RATES.put("Aegean", 1.0);    
        AIRLINE_RATES.put("Lufthansa", 1.2); 
        AIRLINE_RATES.put("British Airways", 1.15); 
        AIRLINE_RATES.put("Air France", 1.1);  
        AIRLINE_RATES.put("KLM", 1.1);         
        AIRLINE_RATES.put("EasyJet", 0.75);    
        AIRLINE_RATES.put("Wizz Air", 0.7);    
        AIRLINE_RATES.put("Turkish Airlines", 1.05); 
    }

    public static double distanceKm(String from, String to){
        if(from == null || to == null) return 500.0;
        double[] a = CITY_COORDS.get(from);
        double[] b = CITY_COORDS.get(to);
        if(a == null || b == null) return 500.0; 
        double lat1 = Math.toRadians(a[0]);
        double lon1 = Math.toRadians(a[1]);
        double lat2 = Math.toRadians(b[0]);
        double lon2 = Math.toRadians(b[1]);
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        double R = 6371.0; 
        double h = Math.sin(dlat/2)*Math.sin(dlat/2) + Math.cos(lat1)*Math.cos(lat2)*Math.sin(dlon/2)*Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1-h));
        return R * c;
    }

   
    public static double calculatePricePerSeat(String from, String to, String airline) {
        try {
           
            double realPrice = fetchSkyScannerPrice(from, to, airline);
            if (realPrice > 0) {
                return realPrice;
            }
        } catch (Exception e) {
            System.out.println("Note: Using calculated price (could not fetch real-time data)");
        }

        
        double d = distanceKm(from, to);
        double price;
        if(d <= 500){
            price = 30 + d * 0.06; 
        } else if(d <= 2000){
            price = 60 + d * 0.08; 
        } else {
            price = 120 + d * 0.10; 
        }
        
       
        double airlineRate = AIRLINE_RATES.getOrDefault(airline, 1.0);
        price *= airlineRate;
        
        
        return Math.round(price * 100.0) / 100.0;
    }

    private static double fetchSkyScannerPrice(String from, String to, String airline) {
        
        if (from == null || to == null || airline == null) {
            return -1;
        }
        return -1;
    }

    
    public static Map<String, Map<String, Double>> getAllCityPrices() {
        Map<String, Map<String, Double>> priceMatrix = new HashMap<>();
        List<String> cities = new ArrayList<>(CITY_COORDS.keySet());
        
        for (String from : cities) {
            Map<String, Double> destinations = new HashMap<>();
            priceMatrix.put(from, destinations);
            
            for (String to : cities) {
                if (!from.equals(to)) {
                    double price = calculatePricePerSeat(from, to, "Aegean");
                    destinations.put(to, price);
                }
            }
        }
        return priceMatrix;
    }

    public static void printPriceMatrix() {
        Map<String, Map<String, Double>> prices = getAllCityPrices();
        List<String> cities = new ArrayList<>(prices.keySet());
        Collections.sort(cities);

        
        System.out.printf("%-12s", "From\\To");
        for (String city : cities) {
            System.out.printf("%-12s", city);
        }
        System.out.println("\n" + "-".repeat(12 * (cities.size() + 1)));

       
        for (String from : cities) {
            System.out.printf("%-12s", from);
            Map<String, Double> destinations = prices.get(from);
            for (String to : cities) {
                if (from.equals(to)) {
                    System.out.printf("%-12s", "---");
                } else {
                    Double price = destinations.get(to);
                    System.out.printf("€%-11.2f", price);
                }
            }
            System.out.println();
        }
    }
}
