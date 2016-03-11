#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>


#define MAX_INI_LINE_LEN 512
#define MAX_ARGS 10
#define MAX_OPTIONS 10


/*
 * Traces fatal errors
 */
void trace_error(JNIEnv *env, char * msg)
{
	if ( (*env).ExceptionOccurred() )
	{
		(*env).ExceptionDescribe();
	}
	(*env).FatalError(msg);
}

/*
 * Main method for starting Bagatur
 */
int main(int argc, char** argv)
{

	//char**			s_options;
	//char**			s_args;
	//char*			s_main_class_name;
	//int				s_options_size;
	//int				s_args_size;


	JavaVMInitArgs	vm_args;
	JavaVM*			jvm;
	JNIEnv*			env;
	jclass			mainClass,stringClass;
	jmethodID		mainMethodId;
	jobjectArray	mainArgs;
	jstring*		args;


	vm_args.version = JNI_VERSION_1_6;
	vm_args.nOptions = 4;//s_options_size;
	vm_args.options = new JavaVMOption[4/*s_options_size*/];
//	int i;
//	for (i=0;i<s_options_size;i++)
//	{
//		vm_args.options[i].optionString = s_options[i];
//	}

	vm_args.options[0].optionString = "-Xms256M";
	vm_args.options[1].optionString = "-Xms256M";
	vm_args.options[2].optionString = "-Dengine.boot.cfg=bagaturchess.engines.bagatur.v100.cfg.EngineConfigBaseImpl";
	vm_args.options[3].optionString = "-Djava.class.path=.\\bin\\BagaturBoard.jar;.\\bin\\BagaturOpening.jar;.\\bin\\BagaturSearch.jar;.\\bin\\BagaturUCI.jar;.\\bin\\BagaturEngines.jar;";



	//bagaturchess.uci.run.Boot
	//bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch

	JNI_CreateJavaVM(&jvm, (void**)&env, (void*)&vm_args);

	stringClass = (*env).FindClass("java/lang/String");
	if (!stringClass) {
		trace_error(env,"Cannot locate class java.lang.String");
		goto final;
	}

	mainClass = (*env).FindClass("bagaturchess/uci/run/Boot");

	if (!mainClass) {
		trace_error(env,"Cannot locate main class bagaturchess.uci.run.Boot");
		goto final;
	}

	mainMethodId = (*env).GetStaticMethodID(mainClass,"main","([Ljava/lang/String;)V");
	if (!mainMethodId) {
		trace_error(env,"Cannot locate 'public void main(String[] args)' method in  bagaturchess.uci.run.Boot");
		goto final;
	}

	mainArgs = (*env).NewObjectArray((jsize)1/*s_args_size*/,stringClass,(jobject)0);
	if (!mainArgs) {
		trace_error(env,"Cannot instantiate main arguments ");
		goto final;
	}

	// init java main method args
	args = new jstring[1/*s_args_size*/];
//	for (i=0;i<s_args_size;i++)
//	{
//		//"bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch"
//		args[i] = (*env).NewStringUTF(s_args[i]);
//		(*env).SetObjectArrayElement(mainArgs,i,args[i]);
//	}

	args[0] = (*env).NewStringUTF("bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch");
	(*env).SetObjectArrayElement(mainArgs,0,args[0]);

	if (!args[0]) {
		trace_error(env,"Cannot instantiate main argument ");
		goto final;
	}

	// call the main method
	(*env).CallStaticVoidMethod(mainClass,mainMethodId,mainArgs);

final:
	(*jvm).DestroyJavaVM();

	return 0;
}