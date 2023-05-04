package repositories;



import models.Transaction;
import application.Utilities.LocalDateTimeFormatterHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionRepository implements Repository<Transaction, String> {

    private final String fileName = "transactions.txt";
    
    @Override
    public List<Transaction> all() throws IOException {
        List<Transaction> transactions = new ArrayList<Transaction>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String row;

        while ((row = bufferedReader.readLine()) != null) {
            String[] data = row.split(",");

            Transaction transaction = new Transaction();
            transaction.setId(data[0]);
            transaction.setDescription(data[1]);

            // enum conversion
//            if (data[2].equalsIgnoreCase(Transaction.Type.INCOME.toString())) {
//                transaction.setType(Transaction.Type.INCOME);
//            } else {
//                transaction.setType(Transaction.Type.OUTCOME);
//            }
            if (data[2].equalsIgnoreCase(Transaction.Category.SALARY.toString())) {
                transaction.setCategory(Transaction.Category.SALARY);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.INVESTMENT_INCOME.toString())) {
                transaction.setCategory(Transaction.Category.INVESTMENT_INCOME);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.OTHER_INCOME.toString())) {
                transaction.setCategory(Transaction.Category.OTHER_INCOME);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.HOUSING.toString())) {
                transaction.setCategory(Transaction.Category.HOUSING);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.UTILITIES.toString())) {
                transaction.setCategory(Transaction.Category.UTILITIES);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.GROCERIES.toString())) {
                transaction.setCategory(Transaction.Category.GROCERIES);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.DINING.toString())) {
                transaction.setCategory(Transaction.Category.DINING);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.CLOTHING.toString())) {
                transaction.setCategory(Transaction.Category.CLOTHING);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.ENTERTAINMENT.toString())) {
                transaction.setCategory(Transaction.Category.ENTERTAINMENT);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.TRANSPORTATION.toString())) {
                transaction.setCategory(Transaction.Category.TRANSPORTATION);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.INSURANCE.toString())) {
                transaction.setCategory(Transaction.Category.INSURANCE);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.HEALTH_WELLNESS.toString())) {
                transaction.setCategory(Transaction.Category.HEALTH_WELLNESS);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.DIGITAL_PRODUCT.toString())) {
                transaction.setCategory(Transaction.Category.DIGITAL_PRODUCT);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.TRAVELING.toString())) {
                transaction.setCategory(Transaction.Category.TRAVELING);
            } else if (data[2].equalsIgnoreCase(Transaction.Category.OTHER_OUTCOME.toString())) {
                transaction.setCategory(Transaction.Category.OTHER_OUTCOME);
            } else {
                // Handle an unrecognized category or throw an exception
            }


            transaction.setAmount(Float.valueOf(data[3]));
            transaction.setCreatedAt(LocalDateTimeFormatterHelper.parse(data[4]));
            transaction.setUserId(data[5]);

            transactions.add(transaction);
        }

        bufferedReader.close();

        return transactions;
    }

    @Override
    public Transaction getById(String uuid) throws IOException {
        Predicate<Transaction> uuidPredicate = d -> d.getId().equalsIgnoreCase(uuid);

        return all().stream()
                .filter(uuidPredicate)
                .findFirst().orElse(null);
    }

    @Override
    public void create(Transaction transaction) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));

        bufferedWriter.write(transaction.toWriteable() + "\n");

        bufferedWriter.close();
    }

    @Override
    public void update(Transaction transaction) throws IOException {
        List<Transaction> transactions = all();

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
        Iterator itr = transactions.iterator();

        while (itr.hasNext()) {
            Transaction t = (Transaction) itr.next();

            if (t.getId().equalsIgnoreCase(transaction.getId())) {
                bufferedWriter.write(transaction.toWriteable() + "\n");
            } else {
                bufferedWriter.write(t.toWriteable() + "\n");
            }
        }

        bufferedWriter.close();
    }

    @Override
    public void delete(Transaction transaction) throws IOException {
        List<Transaction> transactions = all();

        Predicate<Transaction> uuidPredicate = d -> d.getId().equalsIgnoreCase(transaction.getId());
        transactions = transactions.stream()
                .filter(uuidPredicate.negate()) // select transactions except this id
                .collect(Collectors.toList());

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
        Iterator itr = transactions.iterator();

        while (itr.hasNext()) {
            Transaction t = (Transaction) itr.next();
            bufferedWriter.write(t.toWriteable() + "\n");
        }

        bufferedWriter.close();
    }
}
