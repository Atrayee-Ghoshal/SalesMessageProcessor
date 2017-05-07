## This is the Sales Message processing application ##
Sales messages are received and processed along with a report generated.

Assumptions made - 
1. If the total number of messages is less than 50, then the application will stop after processing all the messages and will print adjustments made.
2. If the total number of messages is greater than or equal to 50, then the application will stop after processing 50 messages and will print adjustments made.
3. Logs will be entered to the report generated in the console after every 10th message processed.
4. If the total number of messages is less than 10 or the number of messages in the last iteration is less than 10, 
	then the log will be entered to the report after all the messages of that iteration has been processed and the application will 
	stop running after printing the adjustments made.
5. For Message Type 3, only adjustments are made and sale is not recorded (according to the example in the requirement).
6. If the first message for any product is Message Type 3, then it will be ignored as no previous sale for that product is recorded.

Running the application -
Maven is required to run the application from console.
This is a Spring Boot application, and can be run by the following command from the project folder.

mvn spring-boot:run

Data - Currently, the application is designed to pick messages from the file testdata.csv placed in the resources folder in the application classpath.
The csv file can be modified using the below rules:
Column 1 - Message Type (accepted values - Type 1, Type 2, Type 3)
Column 2 - Product Type
Column 3 - Sale value (accepted values - any Number) if Message is of Type 2
		   Adjustment operation (accepted values - ADD, SUBTRACT, MULTIPLY) if the Message is of Type  3

All necessary validations for the incoming data is present.
We can re-run the application after changing the data to see the modified report in the console. 