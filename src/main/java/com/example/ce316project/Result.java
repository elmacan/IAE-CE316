package com.example.ce316project;

public class Result {
   private boolean compiledSuccessfully;
   private boolean runSuccessfully;
   private boolean outputMatches;
   private StringBuilder errorLog;


   public Result(){
      this.errorLog=new StringBuilder();
   }
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
      return errorLog.toString();
   }


   public void appendErrorLog(String error) {
      if (error == null || error.isEmpty()) return;

      // Eğer log henüz başlatılmamışsa, başlat
      if (errorLog == null) {
         errorLog = new StringBuilder();
      }

      if (error != null && !error.isEmpty()) {
         if (errorLog.length() > 0) {
            errorLog.append("\n"); // Add a newline before appending
         }
         errorLog.append(error);
      }
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

   public void clearErrorLog() {
      // errorLog null olmamalı (constructor'da başlatılıyor), ama kontrol etmek güvenli.
      if (this.errorLog != null) {
         this.errorLog.setLength(0); // StringBuilder'ı sıfırlar
      } else {
         // Bu durumun oluşmaması gerekir.
         this.errorLog = new StringBuilder();
      }
   }
}


