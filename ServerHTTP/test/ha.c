#include<stdio.h>

void main (int argc, char * argv[]){
	printf("<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> </head> <body> <h1> ");
	int i = 0;	
	for(;i<argc;i++){
		printf("%s<br>\n",argv[i]);	
	}           
	printf(" </h1>    </body></html>");
}
