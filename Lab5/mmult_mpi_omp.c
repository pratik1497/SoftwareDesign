#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <errno.h>
#include <math.h>
#include <string.h>
#include "mpi.h"
#include <time.h>
#define min(x, y) ((x)<(y)?(x):(y))

/*
I basically reuse the mpi and omp matrix times vector code by sending each column of
the second matrix as the column "b" and repeat process for number of columns of matrix 2. 
*/

int main(int argc, char* argv[])
{
  int i,j,iter;
  int canMatrixMultiply; //boolean to see if can matrix multipy
  int nrows, ncols,*dimen;
  dimen=(int*)malloc(sizeof(int) * 3);
  double  *b, **c;
  double *buffer, ans;
  int run_index;
  int nruns;
  int myid, master, numprocs;
  double starttime, endtime;
  MPI_Status status;
  int  numsent, sender;
  int anstype, row;
  srand(time(0));
  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &myid);
  master=0;

//Checks to see if arguments greater then 2 
if(argc>2)
{
 //Master will parse throught texts files and dynmatically create arrays.
   if(myid==master)
{
        FILE * fp1; //File Pointer for text 1
        FILE * fp2; //File Pointer for text 2
        int numRow1,numCol1,numRow2,numCol2;
        double *matrix1,*matrix2;

        fp1=fopen(argv[1],"r");
        if( fp1 == NULL)
        {
        printf("\nError Opening File of File does not exist\n");
	//Sends a message to slaves to terminate 
	for(i=0;i<numprocs-1;i++){
          MPI_Send(MPI_BOTTOM, 0, MPI_INT, i+1, 0, MPI_COMM_WORLD);
          }
        exit(0);
        }
        char line[128];
        //Gets row and col values for first file
        if(fgets(line,sizeof line, fp1)!=NULL)
        {
        //Gets first line and parses to get row value
         char *ret;
         ret=strstr(line,"rows(");
         char *value;
         value= strchr(ret,')');
         int index;
         index= (int)(value-ret);
         value=strndup(ret+5,index-5);
         numRow1=atoi(value);
        //Gets column value  
         ret=strstr(ret,"cols(");
         value=strchr(ret,')');
         index=(int)(value-ret);
         value= strndup(ret+5,index-5);
         numCol1=atoi(value);
        }
        printf("\n%d by %d\n",numRow1,numCol1);

 //Allocate Memory & Populate Matrix 1
         matrix1=(double*)malloc(sizeof(double) * numRow1 * numCol1);
        for(i=0;i<numRow1;i++)
        {
         for(j=0;j<numCol1;j++)
                {
               int hold= fscanf(fp1,"%lf",&matrix1[i*numCol1+j]);
                //printf("%lf ",matrix1[i*numCol1+j]);
                }
                //printf("\n");
        }
        //Close File 1 pointer
        fclose(fp1);

        //Open File 2
        fp2=fopen(argv[2],"r");
        if( fp2 == NULL)
        {
                printf("\nError Opening File or File does not exist\n");
               //Sends slave message to exit 
		for(i=0;i<numprocs-1;i++){
         	 MPI_Send(MPI_BOTTOM, 0, MPI_INT, i+1, 0, MPI_COMM_WORLD);
         	 }
		 exit(0);
        }
        //Gets row and col values for second file
        if(fgets(line,sizeof line, fp2)!=NULL)
        {
        //Gets first line and parses to get row value
         char *ret;
         ret=strstr(line,"rows(");
         char *value;
         value= strchr(ret,')');
         int index;
         index= (int)(value-ret);
         value=strndup(ret+5,index-5);
         numRow2=atoi(value);
        //Gets column value  
         ret=strstr(ret,"cols(");
         value=strchr(ret,')');
         index=(int)(value-ret);
         value= strndup(ret+5,index-5);
         numCol2=atoi(value);
        }
        printf("\n%d by %d\n",numRow2,numCol2);
 //Allocate Memory & Populate Matrix 2
         matrix2=(double*)malloc(sizeof(double) * numRow2 * numCol2);
        for(i=0;i<numRow2;i++)
        {
         for(j=0;j<numCol2;j++)
                {
                int hold=fscanf(fp2,"%lf",&matrix2[i*numCol2+j]);
                //printf("%lf ",matrix2[i*numCol2+j]);
                }
                //printf("\n");
        }
        //Close file 2 pointer
        fclose(fp2);

        if(numCol1==numRow2)
                canMatrixMultiply=1;
        else
                canMatrixMultiply=0;

        //ACTUALYY MULTIPY and start distributing rows and columns
        if(canMatrixMultiply==1)
        {
        printf("\nWe can multiply the matrices");
        ncols=numCol1;
        nrows=numRow1;
        dimen[0]=nrows;
        dimen[1]=ncols;
        dimen[2]=numCol2;
        //Sends each slave the neccesary info like row and colmns
        for(i=0;i<numprocs-1;i++){
        MPI_Send(dimen, 3, MPI_INT, i+1, i+1, MPI_COMM_WORLD);
        //printf("\nSENDING NOTFICATION CAN MULTIPY TO SLAVE");
        }
        b = (double*)malloc(sizeof(double) * ncols);
        c = (double **)malloc(numRow1 * sizeof(double *));
        for (i=0; i<numRow1; i++)
         c[i] = (double *)malloc(numCol2 * sizeof(double));
        buffer = (double*)malloc(sizeof(double) * ncols);

	/*
        We will use the matrix times vector as a guideline as we will 
        take each column of second matrix to be our vector and keep sending a new column
        */
        starttime= MPI_Wtime();
        for(iter=0;iter<numCol2;iter++)
         {
                //printf("\n\n\n\nNow on %d column of matrix 2",iter+1);
                /*
                Take each column of second matrix to be b vector and rest is just 
                matrix times vector code
                */
                for( i=0;i<ncols;i++)
                {
                b[i]=matrix2[i*numCol2+iter];
                //printf("\n%f",b[i]);
                }
                numsent = 0;
                 MPI_Bcast(b, ncols, MPI_DOUBLE, master, MPI_COMM_WORLD);
                      for (i = 0; i < min(numprocs-1, nrows); i++) {
                        for (j = 0; j < ncols; j++) {
                          buffer[j] = matrix1[i * ncols + j];
                        }
                        MPI_Send(buffer, ncols, MPI_DOUBLE, i+1, i+1, MPI_COMM_WORLD);
                        numsent++;
                      }
                      for (i = 0; i < nrows; i++) {
                        MPI_Recv(&ans, 1, MPI_DOUBLE, MPI_ANY_SOURCE, MPI_ANY_TAG,
                                 MPI_COMM_WORLD, &status);
                        sender = status.MPI_SOURCE;
                        anstype = status.MPI_TAG;
                        c[anstype-1][iter] = ans;
                        //printf("\nRECIEVED ANS %f",ans);
                        if (numsent < nrows) {
                          for (j = 0; j < ncols; j++) {
                            buffer[j] = matrix1[numsent*ncols + j];
                          }
                          MPI_Send(buffer, ncols, MPI_DOUBLE, sender, numsent+1,
                                   MPI_COMM_WORLD);
                          numsent++;
                        }else {
                          MPI_Send(MPI_BOTTOM, 0, MPI_DOUBLE, sender, 0, MPI_COMM_WORLD);
                         }
                        }
        }
        endtime= MPI_Wtime();
	 printf("\nReached end with time %f",(endtime - starttime));
        FILE * fp3; // File pointer to produce result text file 
        fp3=fopen("result.txt","w");
        fprintf(fp3,"rows(%d) cols(%d)\n",numRow1,numCol2);
        printf("\n\nProduct of two matrices in result.txt\n");
        for(i=0;i<numRow1;i++)
        {
        for(j=0;j<numCol2;j++)
        { fprintf(fp3,"%f ",c[i][j]);
        }
        fprintf(fp3,"\n");
        }
        fclose(fp3);
     }
    //If dimensions not right print out cant multiply and notify all slaves to end 
       else{
        printf("\nDimensions do no allow multiplication\n");
        for(i=0;i<numprocs-1;i++){
          MPI_Send(MPI_BOTTOM, 0, MPI_INT, i+1, 0, MPI_COMM_WORLD);
          }
        }

} else {
 /* SLAVE CODE
 Will recieve certain info on dimenionsion of matrixs and will learn 
 if can actually multiply or just stop slaves
 */
 MPI_Recv(dimen, 3, MPI_INT, master, MPI_ANY_TAG,
                   MPI_COMM_WORLD, &status);
      if(status.MPI_TAG==0){
        canMatrixMultiply==0;
        }
    //If can multiply allocate memory in slave
      else{
        canMatrixMultiply=1;
        nrows=dimen[0];
        ncols=dimen[1];
        iter=dimen[2];
        //printf("\\nRECIEDCE DIMEN %d %d %d",nrows,ncols,iter);
         b = (double*)malloc(sizeof(double) * ncols);
        buffer = (double*)malloc(sizeof(double) * ncols);
        }
    //If can multiply actually multiply by getting a new column each iteration
     if(canMatrixMultiply==1){
            //printf("\n\nIN SLAVE AND CAN MULTPY"); 
           for(i=0;i<iter;i++){
             MPI_Bcast(b, ncols, MPI_DOUBLE, master, MPI_COMM_WORLD);
              if (myid <= nrows) {
                while(1) {
                  MPI_Recv(buffer, ncols, MPI_DOUBLE, master, MPI_ANY_TAG,
                           MPI_COMM_WORLD, &status);
                  if (status.MPI_TAG == 0){
                    break;
                  }
                  row = status.MPI_TAG;
                  ans = 0.0;
        #pragma omp parallel default(none) shared(ncols,buffer,b,ans) private(j)
        #pragma omp for reduction(+:ans)
                  for (j = 0; j < ncols; j++) {
                    ans += buffer[j] * b[j];
                  }
                  MPI_Send(&ans, 1, MPI_DOUBLE, master, row, MPI_COMM_WORLD);
                }
              }
             // printf("\n\nBROKE OUT OF SLAVE WHILE");
            }
       }
  }
}//end of if arg greater that 2

//If not enought arguments print out correct formating
else{
if(myid==master){
fprintf(stderr, "Need two text files  <name1.txt> <name2.txt>\n");
}}
MPI_Finalize();
return 0;

}
