package com.lunasapiens;

public class OpenAiGptConfig {

    private String apiKeyOpenAI;
    private String modelGpt4;
    private String modelGpt3_5;
    private String modelGpt3_5TurboInstruct;


    public OpenAiGptConfig(String apiKeyOpenAI, String modelGpt4, String modelGpt3_5, String modelGpt3_5TurboInstruct) {
        this.apiKeyOpenAI = apiKeyOpenAI;
        this.modelGpt4 = modelGpt4;
        this.modelGpt3_5 = modelGpt3_5;
        this.modelGpt3_5TurboInstruct = modelGpt3_5TurboInstruct;
    }

    public String getApiKeyOpenAI() {
        return apiKeyOpenAI;
    }

    public void setApiKeyOpenAI(String apiKeyOpenAI) {
        this.apiKeyOpenAI = apiKeyOpenAI;
    }

    public String getModelGpt4() {
        return modelGpt4;
    }

    public void setModelGpt4(String modelGpt4) {
        this.modelGpt4 = modelGpt4;
    }

    public String getModelGpt3_5() {
        return modelGpt3_5;
    }

    public void setModelGpt3_5(String modelGpt3_5) {
        this.modelGpt3_5 = modelGpt3_5;
    }

    public String getModelGpt3_5TurboInstruct() {
        return modelGpt3_5TurboInstruct;
    }

    public void setModelGpt3_5TurboInstruct(String modelGpt3_5TurboInstruct) {
        this.modelGpt3_5TurboInstruct = modelGpt3_5TurboInstruct;
    }
}
