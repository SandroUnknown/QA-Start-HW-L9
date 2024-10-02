package tests.models;

import java.util.List;

public class Resume {

    private String name;
    private String dateOfBirth;
    private Gender gender;
    private List<WorkExperience> workExperiences;

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public List<WorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public static class WorkExperience {

        private String companyName;
        private String position;
        private String startDate;
        private String endDate;

        public String getCompanyName() {
            return companyName;
        }

        public String getPosition() {
            return position;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }
    }

    public enum Gender {
        MALE("Male"), FEMALE("Female"), OTHER("Other");

        Gender(String title) {
        }
    }
}