package com.example.ce316project;

public class Result {
   private boolean compiledSuccessfully;
   private boolean runSuccessfully;
   private boolean outputMatches;
   private String errorLog;



   public boolean isCompiledSuccessfully() {
      return compiledSuccessfully;
   }

   public void setCompiledSuccessfully(boolean compiledSuccessfully) {
      this.compiledSuccessfully = compiledSuccessfully;
   }

   public boolean isRunSuccessfully() {
      return runSuccessfully;
   }

   public void setRunSuccessfully(boolean runSuccessfully) {
      this.runSuccessfully = runSuccessfully;
   }

   public boolean isOutputMatches() {
      return outputMatches;
   }

   public void setOutputMatches(boolean outputMatches) {
      this.outputMatches = outputMatches;
   }

   public String getErrorLog() {
      return errorLog;
   }

   public void setErrorLog(String errorLog) {
      this.errorLog = errorLog;
   }

   @Override
   public String toString() {
      return "Result{" +
              "compiledSuccessfully=" + compiledSuccessfully +
              ", runSuccessfully=" + runSuccessfully +
              ", outputMatches=" + outputMatches +
              ", errorLog='" + errorLog + '\'' +
              '}';
   }
}


