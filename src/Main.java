public class Main {
    public static void main(String[] args) {
        Sports sports = new Sports();
        try {
            sports.defineActivities("Trekking", "Running", "Swimming");
            System.out.println("Activities: " + sports.getActivities());
            sports.addCategory("Shorts", "Trekking", "Running");
            sports.addCategory("Swimsuit", "Swimming");
            System.out.println("Number of categories: " + sports.countCategories());
            System.out.println("Categories for Trekking: " + sports.getCategoriesForActivity("Trekking"));
            sports.addProduct("TrekShort1", "Trekking", "Shorts");
            sports.addProduct("SwimGear1", "Swimming", "Swimsuit");
            sports.addProduct("RunShort1", "Running", "Shorts");
            System.out.println("Products for category 'Shorts': " + sports.getProductsForCategory("Shorts"));
            System.out.println("Products for activity 'Trekking': " + sports.getProductsForActivity("Trekking"));
            sports.addRating("TrekShort1", "User1", 4, "Great for hiking!");
            sports.addRating("SwimGear1", "User2", 5, "Perfect fit!");
            sports.addRating("RunShort1", "User3", 3, "Decent, but could be better.");
            System.out.println("Ratings for SwimGear1: " + sports.getRatingsForProduct("SwimGear1"));
            System.out.println("Average stars for TrekShort1: " + sports.getStarsOfProduct("TrekShort1"));
            System.out.println("Overall average stars: " + sports.averageStars());
            System.out.println("Stars per activity: " + sports.starsPerActivity());
            System.out.println("Products per stars: " + sports.getProductsPerStars());
        } catch (SportsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}