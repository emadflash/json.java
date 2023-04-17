package example;

import java.util.ArrayList;
import java.util.HashMap;

import json.JsonSerializable;
import json.JsonSerializer;

class Employee implements JsonSerializable {
    private String name; // Does't matter if its 'public' or 'private' #freespeech
    private int age;
    private String department;
    private int salary;
    private boolean hasKids = false;
    private String aliases[] = { "z03a", "int0x80" };
    public ArrayList<String> skills = new ArrayList<>();
    public HashMap<String, Integer> performanceRating = new HashMap<>();

    Employee(String name, int age, String department, int salary) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.salary = salary;
    }

    public void AddSkill(String skill) {
        this.skills.add(skill);
    }

    public void AddPerformanceRating(String doamin, Integer rating) {
        this.performanceRating.put(doamin, rating);
    }
}

public class serialization {
    public static void main(String args[]) {
        Employee bob = new Employee("Bob Aliceson", 69, "Social Sciences", 40000);

        // add some skills
        bob.AddSkill("C/C++");
        bob.AddSkill("Rust");

        // add some performance rating
        bob.AddPerformanceRating("Frontend", 678);
        bob.AddPerformanceRating("Backend", 123);

        try {
            String ret = JsonSerializer.ToJson(bob);
            System.out.println(ret);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
