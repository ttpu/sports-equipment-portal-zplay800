import java.util.*;
import java.util.stream.Collectors;

public class Sports {
    private TreeSet<String> activities = new TreeSet<>();
    private Set<String> categories = new HashSet<>();
    private Map<String, TreeSet<String>> activityToCategories = new HashMap<>();
    private Map<String, Product> products = new HashMap<>();
    private Map<String, TreeSet<String>> categoryToProducts = new HashMap<>();
    private Map<String, TreeSet<String>> activityToProducts = new HashMap<>();
    private Map<String, Map<String, Rating>> productToRatings = new HashMap<>();

    public void defineActivities(String... activities) throws SportsException {
        if (activities.length == 0) {
            throw new SportsException("No activities provided");
        }
        for (String activity : activities) {
            this.activities.add(activity);
        }
    }

    public List<String> getActivities() {
        return new ArrayList<>(activities);
    }

    public void addCategory(String name, String... linkedActivities) throws SportsException {
        if (categories.contains(name)) {
            throw new SportsException("Category already exists");
        }
        for (String activity : linkedActivities) {
            if (!activities.contains(activity)) {
                throw new SportsException("Unknown activity: " + activity);
            }
        }
        categories.add(name);
        for (String activity : linkedActivities) {
            activityToCategories.computeIfAbsent(activity, k -> new TreeSet<>()).add(name);
        }
    }

    public int countCategories() {
        return categories.size();
    }

    public List<String> getCategoriesForActivity(String activity) {
        TreeSet<String> cats = activityToCategories.get(activity);
        return cats != null ? new ArrayList<>(cats) : new ArrayList<>();
    }

    public void addProduct(String name, String activityName, String categoryName) throws SportsException {
        if (products.containsKey(name)) {
            throw new SportsException("Product already exists");
        }
        if (!activities.contains(activityName)) {
            throw new SportsException("Unknown activity");
        }
        if (!categories.contains(categoryName)) {
            throw new SportsException("Unknown category");
        }
        TreeSet<String> catsForActivity = activityToCategories.get(activityName);
        if (catsForActivity == null || !catsForActivity.contains(categoryName)) {
            throw new SportsException("Category not linked to activity");
        }
        products.put(name, new Product(activityName, categoryName));
        categoryToProducts.computeIfAbsent(categoryName, k -> new TreeSet<>()).add(name);
        activityToProducts.computeIfAbsent(activityName, k -> new TreeSet<>()).add(name);
    }

    public List<String> getProductsForCategory(String categoryName) {
        TreeSet<String> prods = categoryToProducts.get(categoryName);
        return prods != null ? new ArrayList<>(prods) : new ArrayList<>();
    }

    public List<String> getProductsForActivity(String activityName) {
        TreeSet<String> prods = activityToProducts.get(activityName);
        return prods != null ? new ArrayList<>(prods) : new ArrayList<>();
    }

    public List<String> getProducts(String activityName, String... categoryNames) {
        List<String> result = new ArrayList<>();
        Set<String> cats = new HashSet<>(Arrays.asList(categoryNames));
        TreeSet<String> prods = activityToProducts.get(activityName);
        if (prods != null) {
            for (String p : prods) {
                if (cats.contains(products.get(p).category)) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    public void addRating(String productName, String userName, int numStars, String comment) throws SportsException {
        if (numStars < 0 || numStars > 5) {
            throw new SportsException("Invalid number of stars");
        }
        if (!products.containsKey(productName)) {
            throw new SportsException("Product does not exist");
        }
        Map<String, Rating> ratings = productToRatings.computeIfAbsent(productName, k -> new HashMap<>());
        if (ratings.containsKey(userName)) {
            throw new SportsException("User already rated this product");
        }
        ratings.put(userName, new Rating(userName, numStars, comment));
    }

    public List<String> getRatingsForProduct(String productName) {
        Map<String, Rating> ratingsMap = productToRatings.get(productName);
        if (ratingsMap == null) {
            return new ArrayList<>();
        }
        List<Rating> ratings = new ArrayList<>(ratingsMap.values());
        ratings.sort((r1, r2) -> Integer.compare(r2.numStars, r1.numStars));
        return ratings.stream().map(r -> r.numStars + " : " + r.comment).collect(Collectors.toList());
    }

    public double getStarsOfProduct(String productName) {
        Map<String, Rating> ratingsMap = productToRatings.get(productName);
        if (ratingsMap == null || ratingsMap.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Rating r : ratingsMap.values()) {
            sum += r.numStars;
        }
        return sum / ratingsMap.size();
    }

    public double averageStars() {
        if (productToRatings.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        int count = 0;
        for (Map<String, Rating> ratingsMap : productToRatings.values()) {
            for (Rating r : ratingsMap.values()) {
                sum += r.numStars;
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }

    public SortedMap<String, Double> starsPerActivity() {
        Map<String, Double> sumPerActivity = new HashMap<>();
        Map<String, Integer> countPerActivity = new HashMap<>();
        for (String productName : productToRatings.keySet()) {
            Map<String, Rating> ratingsMap = productToRatings.get(productName);
            String activity = products.get(productName).activity;
            for (Rating r : ratingsMap.values()) {
                sumPerActivity.merge(activity, (double) r.numStars, Double::sum);
                countPerActivity.merge(activity, 1, Integer::sum);
            }
        }
        SortedMap<String, Double> result = new TreeMap<>();
        for (String activity : sumPerActivity.keySet()) {
            double sum = sumPerActivity.get(activity);
            int count = countPerActivity.get(activity);
            result.put(activity, sum / count);
        }
        return result;
    }

    public SortedMap<Double, List<String>> getProductsPerStars() {
        Map<String, Double> productToAverage = new HashMap<>();
        for (String productName : productToRatings.keySet()) {
            Map<String, Rating> ratingsMap = productToRatings.get(productName);
            if (!ratingsMap.isEmpty()) {
                double sum = 0;
                for (Rating r : ratingsMap.values()) {
                    sum += r.numStars;
                }
                productToAverage.put(productName, sum / ratingsMap.size());
            }
        }
        SortedMap<Double, List<String>> result = new TreeMap<>(Collections.reverseOrder());
        for (String product : productToAverage.keySet()) {
            double average = productToAverage.get(product);
            result.computeIfAbsent(average, k -> new ArrayList<>()).add(product);
        }
        for (List<String> list : result.values()) {
            Collections.sort(list);
        }
        return result;
    }

    private static class Product {
        String activity;
        String category;

        Product(String activity, String category) {
            this.activity = activity;
            this.category = category;
        }
    }

    private static class Rating {
        String userName;
        int numStars;
        String comment;

        Rating(String userName, int numStars, String comment) {
            this.userName = userName;
            this.numStars = numStars;
            this.comment = comment;
        }
    }
}