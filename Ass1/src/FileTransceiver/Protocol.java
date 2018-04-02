public interface Protocol {
    static final String SUCCESS_CODE = "200";
    static final String FAIL_CODE = "400";
    static final String OPTION_CODE = "204";
    static final String READY_TO_RECEIVE_DATA = "208";

    static final String SEND_FILE_COMMAND = "SF";
    static final String SEND_KEY_COMMAND = "SK";
    //    static final String CONNECTION_ESTABLISED_COMMAND = "CE";
    static final String NONE_DATA = "NONE";
    static final String PROTOCOL_SEPARATOR = "__";
//    static final String DONE="DONE";

/*      FLOW
        Client = = = = = = = = = = = = = = = = Server
	    204_SK_FIZESIZE
					                        	208_SK_NONE
        KEY
	                                            200_SK_NONE
						                        204_SF_FIZESIZE
	    208_SF_NONE
                                                FILE
        200_SF_NONE
*/

    interface ServerToClient {
        //        void Handle200CE();
//        void Handle400CE();
        void Handle200SF();
        void Handle400SK();
        void Handle204SK();
        void Handle208SF();

    }
    interface ClientToServer{
        //        void Handle200SF();
//        void Handle400SF();
//        void Handle200SK();
//        void Handle400SK();
        void Handle208SK();
        void Handle204SF();
        void Handle200SK();
        void Handle400SF();
    }
}

