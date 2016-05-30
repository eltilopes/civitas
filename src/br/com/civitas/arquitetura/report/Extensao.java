package br.com.civitas.arquitetura.report;

public enum Extensao {
	
   PDF{
	   public String getContentType() {
		   return "application/pdf";
	   }
   }, 
   XLS {
	   public String getContentType() {
		   return "application/vnd.ms-excel";
	   } 
   }, 
   HTML {
	   public String getContentType() {
		   return "text/html";
	   }
   }
   , 
   ODT{
	   public String getContentType() {
		   return "application/vnd.oasis.opendocument.text";
	   }
   };
   
   public String getContentType() {
	   throw new AbstractMethodError();
   }
}
