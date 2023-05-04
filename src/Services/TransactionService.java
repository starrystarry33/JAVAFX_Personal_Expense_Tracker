package Services;



import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;

import java.util.LinkedHashMap;
import java.util.Map;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.Transaction;
import models.Transaction.Category;
import models.UserSession;
import repositories.TransactionRepository;

public class TransactionService {

    private TransactionRepository transactionRepository = new TransactionRepository();

    public Transaction create(String description, Float amount, Transaction.Category category, String userId, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setDescription(description);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setCreatedAt(date.atStartOfDay());
        transaction.setUserId(userId);

        try {
            transactionRepository.create(transaction);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transaction;
    }
    
    public void update(String id, String description, Float amount, Transaction.Category category, LocalDateTime createdAt) {
        try {
            Transaction transaction = transactionRepository.getById(id);

            if (transaction != null) {
                transaction.setDescription(description);
                transaction.setAmount(amount);
                transaction.setCategory(category);
                transaction.setCreatedAt(createdAt);
                transactionRepository.update(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id) {
        try {
            Transaction transaction = transactionRepository.getById(id);

            if (transaction != null) {
                transactionRepository.delete(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public List<Data> getPieChartData(String uuid) throws IOException {
//        List<PieChart.Data> pieChartData = new ArrayList<>();
//        List<Transaction> transactions = getByUserId(uuid);
//
//        Predicate<Transaction> incomePredicate = d -> d.getType().equals(Transaction.Type.INCOME);
//        Predicate<Transaction> outcomePredicate = d -> d.getType().equals(Transaction.Type.OUTCOME);
//
//        float incomeSum = transactions.stream().filter(incomePredicate)
//                .collect(Collectors.summingDouble(o -> o.getAmount())).floatValue();
//
//        float outcomeSum = transactions.stream().filter(outcomePredicate)
//                .collect(Collectors.summingDouble(o -> o.getAmount())).floatValue();
//
//        pieChartData.add(new Data("INCOME", Math.floor(incomeSum)));
//        pieChartData.add(new Data("OUTCOME", Math.floor(outcomeSum)));
//
//        return pieChartData;
//    }
    //////////////////////////////////////
    
//    public List<PieChart.Data> getPieChartData(String uuid) throws IOException {
//        List<PieChart.Data> pieChartData = new ArrayList<>();
//        List<Transaction> transactions = getByUserId(uuid);
//
//        List<Transaction.Category> incomeCategories = Arrays.asList(
//            Transaction.Category.SALARY,
//            Transaction.Category.INVESTMENT_INCOME,
//            Transaction.Category.OTHER_INCOME
//        );
//
//        List<Transaction.Category> outcomeCategories = Arrays.asList(
//            Transaction.Category.HOUSING,
//            Transaction.Category.UTILITIES,
//            Transaction.Category.GROCERIES,
//            Transaction.Category.TRANSPORTATION,
//            Transaction.Category.INSURANCE,
//            Transaction.Category.HEALTH_WELLNESS,
//            Transaction.Category.ENTERTAINMENT
//        );
//
//        Predicate<Transaction> incomePredicate = d -> incomeCategories.contains(d.getCategory());
//        Predicate<Transaction> outcomePredicate = d -> outcomeCategories.contains(d.getCategory());
//
//        float incomeSum = transactions.stream().filter(incomePredicate)
//                .collect(Collectors.summingDouble(o -> o.getAmount())).floatValue();
//
//        float outcomeSum = transactions.stream().filter(outcomePredicate)
//                .collect(Collectors.summingDouble(o -> o.getAmount())).floatValue();
//
//        pieChartData.add(new PieChart.Data("INCOME", Math.floor(incomeSum)));
//        pieChartData.add(new PieChart.Data("OUTCOME", Math.floor(outcomeSum)));
//
//        return pieChartData;
//    }
//    }
    public List<Data> getIncomePieChartData(String uuid) throws IOException {
        List<PieChart.Data> incomePieChartData = new ArrayList<>();
        List<Transaction> transactions = getByUserId(uuid);

        // Income categories
        Map<Category, Float> incomeData = new LinkedHashMap<>();
        incomeData.put(Category.SALARY, 0f);
        incomeData.put(Category.INVESTMENT_INCOME, 0f);
        incomeData.put(Category.OTHER_INCOME, 0f);

        transactions.stream()
                .filter(t -> t.getAmount() >= 0)
                .forEach(t -> incomeData.put(t.getCategory(), incomeData.get(t.getCategory()) + t.getAmount()));

        incomeData.forEach((category, sum) -> incomePieChartData.add(new Data(category.toString(), Math.floor(sum))));

        return incomePieChartData;
    }

    public List<Data> getOutcomePieChartData(String uuid) throws IOException {
        List<PieChart.Data> outcomePieChartData = new ArrayList<>();
        List<Transaction> transactions = getByUserId(uuid);

        // Expense categories
        Map<Category, Float> outcomeData = new LinkedHashMap<>();
        outcomeData.put(Category.HOUSING, 0f);
        outcomeData.put(Category.UTILITIES, 0f);
        outcomeData.put(Category.GROCERIES, 0f);
        outcomeData.put(Category.DINING, 0f);
        outcomeData.put(Category.CLOTHING, 0f);
        outcomeData.put(Category.ENTERTAINMENT, 0f);
        outcomeData.put(Category.TRANSPORTATION, 0f);
        outcomeData.put(Category.INSURANCE, 0f);
        outcomeData.put(Category.HEALTH_WELLNESS, 0f);
        outcomeData.put(Category.DIGITAL_PRODUCT, 0f);
        outcomeData.put(Category.TRAVELING, 0f);
        outcomeData.put(Category.OTHER_OUTCOME, 0f);

        transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .forEach(t -> outcomeData.put(t.getCategory(), outcomeData.get(t.getCategory()) - t.getAmount()));

        outcomeData.forEach((category, sum) -> outcomePieChartData.add(new Data(category.toString(), Math.floor(sum))));

        return outcomePieChartData;
    }
    
    public List<Transaction> search(String description, Transaction.Category category) throws IOException {
	    String userId = UserSession.getInstance().getUser().getId();
	        
	    Predicate<Transaction> descriptionPredicate = d -> d.getDescription().contains(description);
	    Predicate<Transaction> categoryPredicate = t -> t.getCategory() == category;
	    Predicate<Transaction> userUuidPredicate = t -> t.getUserId().equalsIgnoreCase(userId);

	    Stream<Transaction> stream = transactionRepository.all().stream()
	            .filter(userUuidPredicate);

	    if (!description.isEmpty()) {
	        stream = stream.filter(descriptionPredicate);
	    }

	    if (category != null) {
	        stream = stream.filter(categoryPredicate);
	    }

	    return stream.collect(Collectors.toList());
	}

/*   
    public List<Transaction> search(String description, Transaction.Category category, LocalDate startDate, LocalDate endDate) throws IOException {
        String userId = UserSession.getInstance().getUser().getId();

        Predicate<Transaction> descriptionPredicate = t -> t.getDescription().contains(description);
        Predicate<Transaction> categoryPredicate = t -> t.getCategory() == category;
        Predicate<Transaction> datePredicate = t -> (t.getCreatedAt().toLocalDate().isAfter(startDate.minusDays(1))) && (t.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1)));
        Predicate<Transaction> userUuidPredicate = t -> t.getUserId().equalsIgnoreCase(userId);

	    Stream<Transaction> stream = transactionRepository.all().stream()
	            .filter(userUuidPredicate);
	    
	    if (!description.isEmpty()) {
	        stream = stream.filter(descriptionPredicate);
	    }

	    if (category != null) {
	        stream = stream.filter(categoryPredicate);
	    }
	    
	    if (startDate != null && endDate != null) {
	        stream = stream.filter(datePredicate);
	    }
	    
	    return stream.collect(Collectors.toList());
    }
*/    
    
    public List<Transaction> getByUserId(String uuid) throws IOException {
        Predicate<Transaction> userUuidPredicate = d -> d.getUserId().equalsIgnoreCase(uuid);

        return transactionRepository.all().stream()
                .filter(userUuidPredicate)
                .collect(Collectors.toList());
    }
}
