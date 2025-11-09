import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        
        try (Scanner sc = new Scanner(System.in, "UTF-8")) {

        System.out.println("Enter 'p' to see price matrix for all cities, or any other key to continue with booking.");
        String choice = sc.nextLine().trim().toLowerCase();
        if (choice.equals("p")) {
            System.out.println("\nFetching current prices for all city pairs...\n");
            Ticket.printPriceMatrix();
            System.exit(0);
        }
        
        Path storage = Paths.get("users.csv");

        try {
            if (!Files.exists(storage)) {
                Files.write(storage, Arrays.asList("name,email,flightNumber,date,from,to,airline,numTotalPassengers,ticketNumber,pricePerSeat,totalPrice"), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            System.err.println("Could not prepare storage: " + e.getMessage());
            return;
        }


        java.util.Map<String, Flight> flights = new java.util.HashMap<>();


        Random random = new Random();
        
        System.out.println("Enter user records. Type 'q' as name to quit.");
        System.out.println("----------------------------------------");
        
        while (true) {
            System.out.print("Passenger name (or 'q' to quit): ");
            String name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("q")) break;

            System.out.print("Email: ");
            String email = sc.nextLine().trim();


            String flightNumber = "931" + String.format("%05d", random.nextInt(100000));
            System.out.println("\nFlight Details");
            System.out.println("----------------------------------------");
            System.out.println("Your Flight Number: " + flightNumber);


            int day, month, year;
            LocalDate flightDate;
            while (true) {
                try {

                    LocalDate today = LocalDate.now();
                    System.out.println("\nAvailable dates for booking:");
                    System.out.println("----------------------------------------");
                    for (int m = 0; m < 3; m++) {
                        LocalDate firstOfMonth = today.plusMonths(m).withDayOfMonth(1);
                        printMonthCalendar(firstOfMonth);
                    }
                    
                    System.out.println("\nPlease select your travel date:");
                    System.out.print("Day (1-31): ");
                    day = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Month (1-12): ");
                    month = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Year (2025 or later): ");
                    year = Integer.parseInt(sc.nextLine().trim());
                    

                    flightDate = LocalDate.of(year, month, day);
                    
                    if (flightDate.isBefore(LocalDate.now())) {
                        System.out.println("Error: Flight date must be in the future. Please try again.");
                        continue;
                    }
                    
                    if (year < 2025) {
                        System.out.println("Error: Year must be 2025 or later. Please try again.");
                        continue;
                    }
                    

                    break;
                    
                } catch (DateTimeException e) {
                    System.out.println("Error: Invalid date. Please check day/month values and try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter numbers only.");
                }
            }
            
            String date = flightDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            System.out.println("Flight date set to: " + date);

            System.out.print("From: ");
            String from = sc.nextLine().trim();

            System.out.print("To: ");
            String to = sc.nextLine().trim();

            System.out.print("Airline: ");
            String airline = sc.nextLine().trim();


            int seatsBooked = 1;


            Flight flight = flights.get(flightNumber);
            if (flight == null) {
                flight = new Flight(flightNumber, date, from, to, airline);
                flights.put(flightNumber, flight);
            }


            boolean added = flight.addPassengers(seatsBooked);
            if (!added) {
                System.out.println("Cannot book: flight is full (capacity 250). Booking skipped.");
                continue;
            }


            String ticketNumber = flightNumber + "-" + (flight.getNumOfCustomers() + 0);


            Ticket.BaggagePackage.printPackages();
            System.out.print("\nSelect baggage package (1-" + Ticket.BaggagePackage.values().length + "): ");
            int baggageChoice = 1; 
            try {
                baggageChoice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Using default (No baggage).");
            }
            Ticket.BaggagePackage selectedBaggage = Ticket.BaggagePackage.fromChoice(baggageChoice);


            double pricePerSeat = Ticket.calculatePricePerSeat(from, to, airline);
            Ticket t = new Ticket(flight, ticketNumber, seatsBooked, pricePerSeat, selectedBaggage);

            double totalPrice = t.getTotalPrice();


            String line = String.join(",",
                escapeCsv(name), escapeCsv(email), escapeCsv(flightNumber), escapeCsv(date),
                escapeCsv(from), escapeCsv(to), escapeCsv(airline), Integer.toString(flight.getNumOfCustomers()),
                escapeCsv(ticketNumber), String.format("%.2f", pricePerSeat), String.format("%.2f", totalPrice)
            );

            try {
                Files.write(storage, Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                System.out.println("Saved: " + t.toString());
            } catch (IOException e) {
                System.err.println("Failed to store record: " + e.getMessage());
            }
        }

            System.out.println("Finished. Records are stored in: " + storage.toAbsolutePath());
        }
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private static void printMonthCalendar(LocalDate date) {
        System.out.printf("\n%s %d%n", date.getMonth(), date.getYear());
        System.out.println("Mo Tu We Th Fr Sa Su");
        
        
        int dayOfWeek = date.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeek; i++) {
            System.out.print("   ");
        }
        
        LocalDate current = date;
        while (current.getMonth() == date.getMonth()) {

            System.out.printf("%2d ", current.getDayOfMonth());
            

            if (current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                System.out.println();
            }
            
            current = current.plusDays(1);
        }
        System.out.println();
    }
}