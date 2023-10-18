package com.portfolio.gymtracker.JacksonMappers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.portfolio.gymtracker.exercise.Exercise;
import com.portfolio.gymtracker.exercise.training.Training;
import com.portfolio.gymtracker.function.Function;
import com.portfolio.gymtracker.function.FunctionGroup;
import com.portfolio.gymtracker.user.AppUser;

/**
 * This class contains json mapping for normal access
 */
@Component
public class NormalMapper {

    Logger logger = LoggerFactory.getLogger(getClass());
    public NormalMapper(){

    }

    public MappingJacksonValue mapUserDetailed(AppUser user){

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(user);

        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept( 
                    "appUserDetails", 
                    "trainingsCount",
                    "userId"
            )).addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.serializeAllExcept(
                "password"
        ));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    public MappingJacksonValue mapUserList(List<AppUser> users){

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(users);

        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept( 
                "appUserDetails",
                "userId"
            )).addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept(
                "username", "fullname", "email"
        ));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    public MappingJacksonValue mapExerciseDetailed(Exercise exercise){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(exercise);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept( 
            "exerciseDetails", 
            "published",
            "exerciseId",
            "functionPerformance",
            "author",
            "functionsIncluded"
        );
        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("ExerciseFilter", filter)
            .addFilter("ExerciseDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"))
            .addFilter("FunctionFilter", SimpleBeanPropertyFilter.filterOutAllExcept("functionId", "functionDetails"))
            .addFilter("FunctionDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("title", "image"));

        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    public MappingJacksonValue mapExerciseList(List<Exercise> exercises){

 
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(exercises);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept( 
            "exerciseDetails", 
            "published",
            "exerciseId",
            "author",
            "functionsIncluded"
        );
        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("ExerciseFilter", filter)
            .addFilter("ExerciseDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"))
            .addFilter("FunctionFilter", SimpleBeanPropertyFilter.filterOutAllExcept("functionId"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }
    
    public MappingJacksonValue mapFunctionGroupDetailed(FunctionGroup functionGroup){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(functionGroup);

        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("FunctionGroupFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionGroupDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionGroupAuthor", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"))
            .addFilter("GroupFunctionsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("title"))
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }
    
    public MappingJacksonValue mapFunctionGroupList(List<FunctionGroup> functionGroupList){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(functionGroupList);

        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("FunctionGroupFilter", SimpleBeanPropertyFilter.serializeAllExcept("functions"))
            .addFilter("FunctionGroupDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionGroupAuthor", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    
    
    
    public MappingJacksonValue mapFunctionDetailed(Function function){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(function);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept( 
            "functionDetails", 
            "functionGroups",
            "published",
            "functionId",
            "author"
        );
        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("FunctionFilter", filter)
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"))
            .addFilter("FunctionDetailsFilter", SimpleBeanPropertyFilter.serializeAll())

            .addFilter("FunctionGroupFilter", SimpleBeanPropertyFilter.serializeAllExcept("functions"))
            .addFilter("FunctionGroupDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionGroupAuthor", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    
    public MappingJacksonValue mapFunctionList(List<Function> functions){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(functions);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept( 
            "functionDetails",
            "functionGroups", 
            "published",
            "functionId",
            "author"
        );
        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("FunctionFilter", filter)
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"))
            .addFilter("FunctionDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionGroupFilter", SimpleBeanPropertyFilter.serializeAllExcept("functions"))
            .addFilter("FunctionGroupDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("FunctionGroupAuthor", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }
    
    public MappingJacksonValue mapTraining(Training training){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(training);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept( 
            "trainingId",
            "trainingDetails",
            "takes",
            "user",
            "exercise"
        );
        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("TrainingFilter", filter)
            .addFilter("TrainingDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("UserFilter", SimpleBeanPropertyFilter.filterOutAllExcept("userId", "appUserDetails"))
            .addFilter("UserDetailsFilter", SimpleBeanPropertyFilter.filterOutAllExcept("username"))
            .addFilter("ExerciseFilter", SimpleBeanPropertyFilter.filterOutAllExcept("exerciseId"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    public MappingJacksonValue mapTrainingList(List<Training> trainings){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(trainings);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept( 
            "trainingDetails",
            "takes", //in future change for performance
            "trainingId",
            "exercise"
        );
        FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("TrainingFilter", filter)
            .addFilter("TrainingDetailsFilter", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("ExerciseFilter", SimpleBeanPropertyFilter.filterOutAllExcept("exerciseId"));
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

}
