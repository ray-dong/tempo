package org.intalio.tempo.workflow.tmsb4p.server;

import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.SimpleAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.tms.server.Utils;
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.intalio.tempo.workflow.tmsb4p.query.TaskView;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;
import org.intalio.tempo.workflow.tmsb4p.server.dao.JPATaskDaoConnectionFactory;
import org.intalio.tempo.workflow.tmsb4p.server.dao.TaskQueryType;

import com.intalio.wsHT.TOrganizationalEntity;
import com.intalio.wsHT.TUserlist;
import com.intalio.wsHT.api.TStatus;
import com.intalio.wsHT.api.TTask;
import com.intalio.wsHT.api.TTaskAbstract;
import com.intalio.wsHT.api.TTaskQueryResultRow;
import com.intalio.wsHT.api.TTaskQueryResultSet;
import com.intalio.wsHT.api.xsd.ActivateDocument;
import com.intalio.wsHT.api.xsd.ActivateResponseDocument;
import com.intalio.wsHT.api.xsd.AddAttachmentDocument;
import com.intalio.wsHT.api.xsd.AddAttachmentResponseDocument;
import com.intalio.wsHT.api.xsd.AddCommentDocument;
import com.intalio.wsHT.api.xsd.AddCommentResponseDocument;
import com.intalio.wsHT.api.xsd.ClaimDocument;
import com.intalio.wsHT.api.xsd.ClaimResponseDocument;
import com.intalio.wsHT.api.xsd.CompleteDocument;
import com.intalio.wsHT.api.xsd.CompleteResponseDocument;
import com.intalio.wsHT.api.xsd.CreateResponseDocument;
import com.intalio.wsHT.api.xsd.DelegateDocument;
import com.intalio.wsHT.api.xsd.DelegateResponseDocument;
import com.intalio.wsHT.api.xsd.DeleteAttachmentsDocument;
import com.intalio.wsHT.api.xsd.DeleteFaultDocument;
import com.intalio.wsHT.api.xsd.DeleteOutputDocument;
import com.intalio.wsHT.api.xsd.FailDocument;
import com.intalio.wsHT.api.xsd.FailResponseDocument;
import com.intalio.wsHT.api.xsd.ForwardDocument;
import com.intalio.wsHT.api.xsd.ForwardResponseDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosResponseDocument;
import com.intalio.wsHT.api.xsd.GetAttachmentsDocument;
import com.intalio.wsHT.api.xsd.GetCommentsDocument;
import com.intalio.wsHT.api.xsd.GetCommentsResponseDocument;
import com.intalio.wsHT.api.xsd.GetFaultDocument;
import com.intalio.wsHT.api.xsd.GetMyTaskAbstractsDocument;
import com.intalio.wsHT.api.xsd.GetMyTaskAbstractsResponseDocument;
import com.intalio.wsHT.api.xsd.GetMyTasksDocument;
import com.intalio.wsHT.api.xsd.GetMyTasksResponseDocument;
import com.intalio.wsHT.api.xsd.GetOutputDocument;
import com.intalio.wsHT.api.xsd.GetTaskDescriptionDocument;
import com.intalio.wsHT.api.xsd.GetTaskInfoDocument;
import com.intalio.wsHT.api.xsd.NominateDocument;
import com.intalio.wsHT.api.xsd.QueryDocument;
import com.intalio.wsHT.api.xsd.QueryResponseDocument;
import com.intalio.wsHT.api.xsd.ReleaseDocument;
import com.intalio.wsHT.api.xsd.ReleaseResponseDocument;
import com.intalio.wsHT.api.xsd.RemoveDocument;
import com.intalio.wsHT.api.xsd.RemoveResponseDocument;
import com.intalio.wsHT.api.xsd.ResumeDocument;
import com.intalio.wsHT.api.xsd.ResumeResponseDocument;
import com.intalio.wsHT.api.xsd.SetFaultDocument;
import com.intalio.wsHT.api.xsd.SetGenericHumanRoleDocument;
import com.intalio.wsHT.api.xsd.SetOutputDocument;
import com.intalio.wsHT.api.xsd.SetOutputResponseDocument;
import com.intalio.wsHT.api.xsd.SetPriorityDocument;
import com.intalio.wsHT.api.xsd.SetPriorityResponseDocument;
import com.intalio.wsHT.api.xsd.SkipDocument;
import com.intalio.wsHT.api.xsd.SkipResponseDocument;
import com.intalio.wsHT.api.xsd.StartDocument;
import com.intalio.wsHT.api.xsd.StartResponseDocument;
import com.intalio.wsHT.api.xsd.StopDocument;
import com.intalio.wsHT.api.xsd.StopResponseDocument;
import com.intalio.wsHT.api.xsd.SuspendDocument;
import com.intalio.wsHT.api.xsd.SuspendResponseDocument;
import com.intalio.wsHT.api.xsd.SuspendUntilDocument;
import com.intalio.wsHT.api.xsd.SuspendUntilResponseDocument;
import com.intalio.wsHT.api.xsd.TTime;
import com.intalio.wsHT.api.xsd.AddAttachmentDocument.AddAttachment;
import com.intalio.wsHT.api.xsd.AddCommentDocument.AddComment;
import com.intalio.wsHT.api.xsd.CompleteDocument.Complete;
import com.intalio.wsHT.api.xsd.DelegateDocument.Delegate;
import com.intalio.wsHT.api.xsd.DeleteAttachmentsDocument.DeleteAttachments;
import com.intalio.wsHT.api.xsd.ForwardDocument.Forward;
import com.intalio.wsHT.api.xsd.GetAttachmentInfosDocument.GetAttachmentInfos;
import com.intalio.wsHT.api.xsd.GetAttachmentsDocument.GetAttachments;
import com.intalio.wsHT.api.xsd.GetMyTaskAbstractsDocument.GetMyTaskAbstracts;
import com.intalio.wsHT.api.xsd.GetMyTasksDocument.GetMyTasks;
import com.intalio.wsHT.api.xsd.GetOutputDocument.GetOutput;
import com.intalio.wsHT.api.xsd.GetTaskDescriptionDocument.GetTaskDescription;
import com.intalio.wsHT.api.xsd.NominateDocument.Nominate;
import com.intalio.wsHT.api.xsd.QueryDocument.Query;
import com.intalio.wsHT.api.xsd.SetFaultDocument.SetFault;
import com.intalio.wsHT.api.xsd.SetGenericHumanRoleDocument.SetGenericHumanRole;
import com.intalio.wsHT.api.xsd.SetOutputDocument.SetOutput;
import com.intalio.wsHT.api.xsd.SetPriorityDocument.SetPriority;
import com.intalio.wsHT.api.xsd.SuspendUntilDocument.SuspendUntil;

public class TMSRequestProcessorTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        TMSRequestProcessorTest t = new TMSRequestProcessorTest();

    }

    public static TaskPermissions getMeADefaultPermissionHandler() {
        Map<String, Set<String>> permissions = new HashMap<String, Set<String>>();
        AuthIdentifierSet deletePermissions = new AuthIdentifierSet();
        deletePermissions.add("test/system-user");
        permissions.put(TaskPermissions.ACTION_DELETE, deletePermissions);
        return new TaskPermissions(permissions);
    }

    public static SimpleAuthProvider getMeASimpleAuthProvider() {
        UserRoles user1 = new UserRoles("test/user1", new String[] { "test/role1", "test/role2" });
        UserRoles user2 = new UserRoles("test/user2", new String[] { "test/role2", "test/role3" });
        UserRoles user3 = new UserRoles("test/user3", new String[] { "test/role4", "test/role5" });
        UserRoles systemUser = new UserRoles("test/system-user", new String[] { "test/role1", "test/role2", "test/role3", "examples/employee", "*/*" });
        UserRoles systemUser2 = new UserRoles("intalio/manager", new String[] { "test/role1", "test/role2", "test/role3", "examples/employee", "*/*" });
        UserRoles systemUser3 = new UserRoles("intalio/admin", new String[] { "test/role1", "test/role2", "test/role3", "examples/employee", "*/*" });

        SimpleAuthProvider authProvider = new SimpleAuthProvider();
        authProvider.addUserToken("token1", user1);
        authProvider.addUserToken("token2", user2);
        authProvider.addUserToken("token3", user3);
        authProvider.addUserToken("system-user-token", systemUser);
        authProvider.addUserToken("intalio/manager", systemUser2);
        authProvider.addUserToken("intalio/admin", systemUser3);
        return authProvider;
    }

    private TMSRequestProcessor createRequestProcessorJPA() throws Exception {
        ITMSServer server = new TMSServer(getMeASimpleAuthProvider(), new JPATaskDaoConnectionFactory(), getMeADefaultPermissionHandler());
        TMSRequestProcessor proc = new TMSRequestProcessor() {
            protected String getParticipantToken() throws AxisFault {
                // return
                // "VE9LRU4mJnVzZXI9PWFkbWluJiZpc3N1ZWQ9PTExODA0NzY2NjUzOTMmJnJvbGVzPT1pbnRhbGlvXHByb2Nlc3NhZG1pbmlzdHJhdG9yLGV4YW1wbGVzXGVtcGxveWVlLGludGFsaW9ccHJvY2Vzc21hbmFnZXIsZXhhbXBsZXNcbWFuYWdlciYmZnVsbE5hbWU9PUFkbWluaW5pc3RyYXRvciYmZW1haWw9PWFkbWluQGV4YW1wbGUuY29tJiZub25jZT09NDMxNjAwNTE5NDM5MTk1MDMzMyYmdGltZXN0YW1wPT0xMTgwNDc2NjY1Mzk1JiZkaWdlc3Q9PTVmM1dQdDBXOEp2UlpRM2gyblJ6UkRrenRwTT0mJiYmVE9LRU4";
                return "intalio/manager";
            }
        };
        proc.setServer(server);
        return proc;
    }

    public static OMElement loadElementFromResource(String resource) throws Exception {
        InputStream requestInputStream = Utils.class.getResourceAsStream(resource);

        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(requestInputStream);
        StAXOMBuilder builder = new StAXOMBuilder(parser);

        return builder.getDocumentElement();
    }

    private String getTaskId(OMElement res) throws Exception {
        CreateResponseDocument doc = CreateResponseDocument.Factory.parse(res.getXMLStreamReader());
        String out = doc.getCreateResponse().getOut();
        String taskId = out.substring(1, out.length() - 1);
        System.out.println("task id: " + taskId);
        return taskId;
    }

    private String createTask(String reqFile) throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        OMElement requestElement = loadElementFromResource(reqFile);
        OMElement res = tmsRP.create(requestElement);
        return getTaskId(res);
    }

    private OMElement genOMElement(XmlObject xmlObject) {
        OMElement dm = null;
        InputStream is = xmlObject.newInputStream();
        StAXOMBuilder builder;
        try {
            builder = new StAXOMBuilder(is);
            dm = builder.getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dm;
    }

    /************************************************************
     * Test case for participant Operation
     *************************************************************/
    public void testCreate() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        OMElement requestElement = loadElementFromResource("/B4PRequest/create.xml");
        OMElement res = tmsRP.create(requestElement);
        System.out.println("taskid:" + getTaskId(res));

    }

    public void testClaim() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        OMElement res = tmsRP.claim(requestElement);
        ClaimResponseDocument resDoc = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:" + resDoc.xmlText());

    }

    public void testStart() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
        res = tmsRP.start(genOMElement(startReq));
        StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:" + startRes.xmlText());

    }

    public void testStop() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
        res = tmsRP.start(genOMElement(startReq));
        StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:" + startRes.xmlText());

        // stop
        StopDocument stopReq = StopDocument.Factory.newInstance();
        stopReq.addNewStop().setIdentifier(taskId);
        res = tmsRP.stop(genOMElement(stopReq));
        StopResponseDocument stopRes = StopResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:" + stopRes.xmlText());
    }

    public void testComplete() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
        res = tmsRP.start(genOMElement(startReq));
        StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + startRes.xmlText());

        System.out.println("response:" + startRes.xmlText());
        // Complete
        CompleteDocument completeReq = CompleteDocument.Factory.newInstance();
        Complete complete = completeReq.addNewComplete();
        complete.setIdentifier(taskId);
        XmlObject taskData = XmlObject.Factory.parse("<out>sample output</out>");
        complete.setTaskData(taskData);
        res = tmsRP.complete(genOMElement(completeReq));
        CompleteResponseDocument completeRes = CompleteResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:" + completeRes.xmlText());
    }

    public void testFail() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
        res = tmsRP.start(genOMElement(startReq));
        StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + startRes.xmlText());

        // fail
        FailDocument failReq = FailDocument.Factory.newInstance();
        failReq.addNewFail().setIdentifier(taskId);
        res = tmsRP.fail(genOMElement(failReq));
        FailResponseDocument failRes = FailResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + failRes.xmlText());
    }

    public void testRelease() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // release
        ReleaseDocument releaseReq = ReleaseDocument.Factory.newInstance();
        releaseReq.addNewRelease().setIdentifier(taskId);
        res = tmsRP.release(genOMElement(releaseReq));
        ReleaseResponseDocument releaseRes = ReleaseResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + releaseRes.xmlText());
    }

    public void testSkip() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // skip
        SkipDocument skipReq = SkipDocument.Factory.newInstance();
        skipReq.addNewSkip().setIdentifier(taskId);
        res = tmsRP.skip(genOMElement(skipReq));
        SkipResponseDocument skipRes = SkipResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + skipRes.xmlText());
    }

    public void testRemove() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // remove
        RemoveDocument removeReq = RemoveDocument.Factory.newInstance();
        removeReq.addNewRemove().setIdentifier(taskId);
        res = tmsRP.remove(genOMElement(removeReq));
        RemoveResponseDocument removeRes = RemoveResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + removeRes.xmlText());
    }

    public void testSuspendAndResume() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // suspend
        SuspendDocument suspendReq = SuspendDocument.Factory.newInstance();
        suspendReq.addNewSuspend().setIdentifier(taskId);
        res = tmsRP.suspend(genOMElement(suspendReq));
        SuspendResponseDocument suspendRes = SuspendResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + suspendRes.xmlText());

        // resume
        ResumeDocument resumeReq = ResumeDocument.Factory.newInstance();
        resumeReq.addNewResume().setIdentifier(taskId);
        res = tmsRP.resume(genOMElement(resumeReq));
        ResumeResponseDocument resumeRes = ResumeResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + resumeRes.xmlText());
    }

    public void testSuspendUntillAndResume() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // suspendUntil
        SuspendUntilDocument suspendUntilReq = SuspendUntilDocument.Factory.newInstance();
        SuspendUntil s = suspendUntilReq.addNewSuspendUntil();
        s.setIdentifier(taskId);
        TTime tm = TTime.Factory.parse("<timePeriod>PT30S</timePeriod>");
        s.setTime(tm);
        res = tmsRP.suspendUntil(genOMElement(suspendUntilReq));
        SuspendUntilResponseDocument suspendUntilRes = SuspendUntilResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + suspendUntilRes.xmlText());

        // resume
        ResumeDocument resumeReq = ResumeDocument.Factory.newInstance();
        resumeReq.addNewResume().setIdentifier(taskId);
        res = tmsRP.resume(genOMElement(resumeReq));
        ResumeResponseDocument resumeRes = ResumeResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + resumeRes.xmlText());
    }

    public void testForward() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // forward
        ForwardDocument forwardReq = ForwardDocument.Factory.newInstance();
        Forward f = forwardReq.addNewForward();
        f.setIdentifier(taskId);
        TOrganizationalEntity _ba = TOrganizationalEntity.Factory.newInstance();
        TUserlist users = TUserlist.Factory.newInstance();
        users.addUser("intalio/admin");
        _ba.setUsers(users);
        f.setOrganizationalEntity(_ba);

        res = tmsRP.forward(genOMElement(forwardReq));
        ForwardResponseDocument forwardRes = ForwardResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + forwardRes.xmlText());

    }

    public void testDelegate() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + claimRes.xmlText());

        // delegate
        DelegateDocument delegate = DelegateDocument.Factory.newInstance();
        Delegate f = delegate.addNewDelegate();
        f.setIdentifier(taskId);
        TOrganizationalEntity _ba = TOrganizationalEntity.Factory.newInstance();
        TUserlist users = TUserlist.Factory.newInstance();
        users.addUser("intalio/admin");
        _ba.setUsers(users);

        f.setOrganizationalEntity(_ba);

        res = tmsRP.delegate(genOMElement(delegate));
        DelegateResponseDocument delegateRes = DelegateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + delegateRes.xmlText());

    }

    /************************************************************
     * Test case for Task Manipulation Operation
     *************************************************************/
    public void testSetPriority() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        SetPriorityDocument spdReq = SetPriorityDocument.Factory.newInstance();
        SetPriority sp = spdReq.addNewSetPriority();
        sp.setIdentifier(taskId);
        sp.setPriority(BigInteger.valueOf(1));
        OMElement resp = tmsRP.setPriority(genOMElement(spdReq));
        SetPriorityResponseDocument sprd = SetPriorityResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("setOutput response:" + sprd.xmlText());

        // check the query result
        TTaskQueryResultRow resultRow = getTask(tmsRP, taskId);
        assertEquals(resultRow.getPriorityArray(0), BigInteger.valueOf(1));
    }

    public void testAddAttachment() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        AddAttachmentDocument aadReq = AddAttachmentDocument.Factory.newInstance();
        AddAttachment aa = aadReq.addNewAddAttachment();
        aa.setAccessType("inline");
        aa.setContentType("text/plain");
        aa.setIdentifier(taskId);
        aa.setName("test_attachment_1");
        aa.setAttachment(XmlObject.Factory.parse("<data>This should be the attachment inline content</data>"));

        OMElement resp = tmsRP.addAttachment(genOMElement(aadReq));
        AddAttachmentResponseDocument aard = AddAttachmentResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("AddAttachment response:" + aard.xmlText());

        GetAttachmentInfosDocument req = GetAttachmentInfosDocument.Factory.newInstance();
        GetAttachmentInfos gai = req.addNewGetAttachmentInfos();
        gai.setIdentifier(taskId);

        resp = tmsRP.getAttachmentInfos(genOMElement(req));
        GetAttachmentInfosResponseDocument gaird = GetAttachmentInfosResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("Get attachmentInfos response:" + gaird.xmlText());
    }

    public void testGetAttachmentInfos() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        AddAttachmentDocument aadReq = AddAttachmentDocument.Factory.newInstance();
        AddAttachment aa = aadReq.addNewAddAttachment();
        aa.setAccessType("inline");
        aa.setContentType("text/plain");
        aa.setIdentifier(taskId);
        aa.setName("test_attachment_2");
        aa.setAttachment(XmlObject.Factory.parse("<data>This should be the attachment inline content</data>"));

        OMElement resp = tmsRP.addAttachment(genOMElement(aadReq));
        AddAttachmentResponseDocument aard = AddAttachmentResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("AddAttachment response:" + aard.xmlText());

        GetAttachmentInfosDocument req = GetAttachmentInfosDocument.Factory.newInstance();
        GetAttachmentInfos gai = req.addNewGetAttachmentInfos();
        gai.setIdentifier(taskId);

        resp = tmsRP.getAttachmentInfos(genOMElement(req));
        GetAttachmentInfosResponseDocument gaird = GetAttachmentInfosResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("Get attachmentInfos response:" + gaird.xmlText());
    }

    public void testGetAttachments() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        tmsRP.claim(genOMElement(doc));

        AddAttachmentDocument aadReq = AddAttachmentDocument.Factory.newInstance();
        AddAttachment aa = aadReq.addNewAddAttachment();
        aa.setAccessType("inline");
        aa.setContentType("text/plain");
        aa.setIdentifier(taskId);
        aa.setName("test_attachment_2");
        aa.setAttachment(XmlObject.Factory.parse("<data>This should be the attachment inline content</data>"));

        tmsRP.addAttachment(genOMElement(aadReq));
        aa.setName("test_attachment_1");
        aa.setAttachment(XmlObject.Factory.parse("<data>This should be the attachment inline content11</data>"));
        tmsRP.addAttachment(genOMElement(aadReq));

        GetAttachmentsDocument gadReq = GetAttachmentsDocument.Factory.newInstance();
        GetAttachments gad = gadReq.addNewGetAttachments();
        gad.setIdentifier(taskId);
        gad.setAttachmentName("test_attachment_1");

        OMElement resp = tmsRP.getAttachments(genOMElement(gadReq));
        com.intalio.wsHT.api.xsd.GetAttachmentsResponseDocument gard = com.intalio.wsHT.api.xsd.GetAttachmentsResponseDocument.Factory.parse(resp
                        .getXMLStreamReader());
        System.out.println("GetAttachments response:" + gard.xmlText());

    }

    public void testDeleteAttachments() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        tmsRP.claim(genOMElement(doc));

        AddAttachmentDocument aadReq = AddAttachmentDocument.Factory.newInstance();
        AddAttachment aa = aadReq.addNewAddAttachment();
        aa.setAccessType("inline");
        aa.setContentType("text/plain");
        aa.setIdentifier(taskId);
        aa.setName("test_attachment_2");
        aa.setAttachment(XmlObject.Factory.parse("<data>This should be the attachment inline content</data>"));

        tmsRP.addAttachment(genOMElement(aadReq));
        aa.setName("test_attachment_1");
        aa.setAttachment(XmlObject.Factory.parse("<data>This should be the attachment inline content11</data>"));
        tmsRP.addAttachment(genOMElement(aadReq));

        DeleteAttachmentsDocument dadReq = DeleteAttachmentsDocument.Factory.newInstance();
        DeleteAttachments da = dadReq.addNewDeleteAttachments();
        da.setIdentifier(taskId);
        da.setAttachmentName("test_attachment_2");
        tmsRP.deleteAttachments(genOMElement(dadReq));

        GetAttachmentsDocument gadReq = GetAttachmentsDocument.Factory.newInstance();
        GetAttachments gad = gadReq.addNewGetAttachments();
        gad.setIdentifier(taskId);
        gad.setAttachmentName("test_attachment_2");

        OMElement resp = tmsRP.getAttachments(genOMElement(gadReq));
        com.intalio.wsHT.api.xsd.GetAttachmentsResponseDocument gard = com.intalio.wsHT.api.xsd.GetAttachmentsResponseDocument.Factory.parse(resp
                        .getXMLStreamReader());
        System.out.println("GetAttachments test_attachment_2 response:" + gard.xmlText());

    }

    public void testAddComment() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        tmsRP.claim(genOMElement(doc));

        AddCommentDocument acdReq = AddCommentDocument.Factory.newInstance();
        AddComment ac = acdReq.addNewAddComment();
        ac.setIdentifier(taskId);
        ac.setText("This is so nice!");

        OMElement resp = tmsRP.addComment(genOMElement(acdReq));
        AddCommentResponseDocument acrd = AddCommentResponseDocument.Factory.parse(resp.getXMLStreamReader());

        System.out.println("AddComment response: " + acrd.xmlText());

        GetCommentsDocument gcdReq = GetCommentsDocument.Factory.newInstance();
        gcdReq.addNewGetComments().setIdentifier(taskId);

        resp = tmsRP.getComments(genOMElement(gcdReq));
        GetCommentsResponseDocument gcrd = GetCommentsResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("GetComments response:" + gcrd.xmlText());
    }

    public void testGetComments() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        tmsRP.claim(genOMElement(doc));

        AddCommentDocument acdReq = AddCommentDocument.Factory.newInstance();
        AddComment ac = acdReq.addNewAddComment();
        ac.setIdentifier(taskId);
        ac.setText("This is so nice!");

        tmsRP.addComment(genOMElement(acdReq));
        ac.setText("This should be the second comment");
        tmsRP.addComment(genOMElement(acdReq));

        GetCommentsDocument gcdReq = GetCommentsDocument.Factory.newInstance();
        gcdReq.addNewGetComments().setIdentifier(taskId);

        OMElement resp = tmsRP.getComments(genOMElement(gcdReq));
        GetCommentsResponseDocument gcrd = GetCommentsResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("GetComments response(shoud be 2):" + gcrd.xmlText());
    }

    public void testGetTaskInfo() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        GetTaskInfoDocument gtiReq = GetTaskInfoDocument.Factory.newInstance();
        gtiReq.addNewGetTaskInfo().setIdentifier(taskId);

        OMElement resp = tmsRP.getTaskInfo(genOMElement(gtiReq));
        System.out.println("GetTaskInfo response:" + resp.toString());
    }

    public void testGetTaskDescription() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        GetTaskDescriptionDocument gtdReq = GetTaskDescriptionDocument.Factory.newInstance();
        GetTaskDescription gtd = gtdReq.addNewGetTaskDescription();
        gtd.setIdentifier(taskId);
        gtd.setContentType("plain/text");

        OMElement resp = tmsRP.getTaskDescription(genOMElement(gtdReq));
        System.out.println("GetTaskDescription response:" + resp.toString());
    }

    public void testSetOutput() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        // setOutput
        SetOutputDocument sodReq = SetOutputDocument.Factory.newInstance();
        SetOutput so = sodReq.addNewSetOutput();
        so.setIdentifier(taskId);
        so.setPart("par1");
        so.setTaskData(XmlObject.Factory.parse("<part-data>This is the part1 data</part-data>"));
        OMElement requestElement = genOMElement(sodReq);
        OMElement resp = tmsRP.setOutput(requestElement);
        SetOutputResponseDocument sord = SetOutputResponseDocument.Factory.parse(resp.getXMLStreamReader());
        System.out.println("setOutput response:" + sord.xmlText());

        // check with getOuput
        GetOutputDocument godReq = GetOutputDocument.Factory.newInstance();
        GetOutput go = godReq.addNewGetOutput();
        go.setIdentifier(taskId);
        go.setPart("part1");

        resp = tmsRP.getOutput(genOMElement(godReq));
        System.out.println("GetOutput response: " + resp.toString());
    }

    public void testDeleteOutput() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        // setOutput
        SetOutputDocument sodReq = SetOutputDocument.Factory.newInstance();
        SetOutput so = sodReq.addNewSetOutput();
        so.setIdentifier(taskId);
        so.setPart("par1");
        so.setTaskData(XmlObject.Factory.parse("<part-data>This is the part1 data</part-data>"));
        OMElement requestElement = genOMElement(sodReq);
        tmsRP.setOutput(requestElement);

        DeleteOutputDocument dodReq = DeleteOutputDocument.Factory.newInstance();
        dodReq.addNewDeleteOutput().setIdentifier(taskId);

        OMElement resp = tmsRP.deleteOutput(genOMElement(dodReq));
        System.out.println("DeleteOutput response: " + resp.toString());

        // check with getOuput
        GetOutputDocument godReq = GetOutputDocument.Factory.newInstance();
        GetOutput go = godReq.addNewGetOutput();
        go.setIdentifier(taskId);
        go.setPart("part1");

        resp = tmsRP.getOutput(genOMElement(godReq));
        System.out.println("GetOutput response: " + resp.toString());

    }

    public void testSetFault() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        SetFaultDocument sfd = SetFaultDocument.Factory.newInstance();
        SetFault sf = sfd.addNewSetFault();
        sf.setIdentifier(taskId);
        sf.setFaultName("fault1");
        sf.setFaultData(XmlObject.Factory.parse("<fault-data>This is the first fault</fault-data>"));

        OMElement resp = tmsRP.setFault(genOMElement(sfd));
        System.out.println("SetFault response:" + resp.toString());

        GetFaultDocument gfdReq = GetFaultDocument.Factory.newInstance();
        gfdReq.addNewGetFault().setIdentifier(taskId);

        resp = tmsRP.getFault(genOMElement(gfdReq));
        System.out.println("GetFault response: " + resp.toString());
    }

    public void testDeleteFault() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        SetFaultDocument sfd = SetFaultDocument.Factory.newInstance();
        SetFault sf = sfd.addNewSetFault();
        sf.setIdentifier(taskId);
        sf.setFaultName("fault1");
        sf.setFaultData(XmlObject.Factory.parse("<fault-data>This is the first fault</fault-data>"));

        tmsRP.setFault(genOMElement(sfd));

        DeleteFaultDocument dfdReq = DeleteFaultDocument.Factory.newInstance();
        dfdReq.addNewDeleteFault().setIdentifier(taskId);
        
        OMElement resp = tmsRP.deleteFault(genOMElement(dfdReq));
        System.out.println("DeleteFault response:" + resp.toString());
        
        GetFaultDocument gfdReq = GetFaultDocument.Factory.newInstance();
        gfdReq.addNewGetFault().setIdentifier(taskId);

        resp = tmsRP.getFault(genOMElement(gfdReq));
        System.out.println("GetFault response: " + resp.toString());
    }
    
    public void testGetInput() throws Exception{
        //TODO can not be tested right now, but the code is almost the same as getOutput
    }
    
    public void testGetOutput() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        // setOutput
        SetOutputDocument sodReq = SetOutputDocument.Factory.newInstance();
        SetOutput so = sodReq.addNewSetOutput();
        so.setIdentifier(taskId);
        so.setPart("par1");
        so.setTaskData(XmlObject.Factory.parse("<part-data>This is the part1 data</part-data>"));
        
        tmsRP.setOutput(genOMElement(sodReq));
        so.setPart("part2");
        so.setTaskData(XmlObject.Factory.parse("<part-data2>This is the second part data</part-data2>"));
        tmsRP.setOutput(genOMElement(sodReq));
        
        // check with getOuput
        GetOutputDocument godReq = GetOutputDocument.Factory.newInstance();
        GetOutput go = godReq.addNewGetOutput();
        go.setIdentifier(taskId);
        go.setPart("part2");

        OMElement resp = tmsRP.getOutput(genOMElement(godReq));
        System.out.println("GetOutput response part2: " + resp.toString());
    }
    
    public void testGetFault() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement res = tmsRP.claim(genOMElement(doc));
        ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());

        SetFaultDocument sfd = SetFaultDocument.Factory.newInstance();
        SetFault sf = sfd.addNewSetFault();
        sf.setIdentifier(taskId);
        sf.setFaultName("fault1");
        sf.setFaultData(XmlObject.Factory.parse("<fault-data>This is the first fault</fault-data>"));

        tmsRP.setFault(genOMElement(sfd));

        GetFaultDocument gfdReq = GetFaultDocument.Factory.newInstance();
        gfdReq.addNewGetFault().setIdentifier(taskId);

        OMElement resp = tmsRP.getFault(genOMElement(gfdReq));
        System.out.println("GetFault response(should be 2): " + resp.toString());
    }

    /************************************************************
     * Test case for admin/query Operation
     *************************************************************/
    public void testActivate() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // check point
        long beforeActivateTime = System.currentTimeMillis();

        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:" + activateRes.xmlText());

        // query the task to check the task status and activate time
        QueryDocument queryReq = QueryDocument.Factory.newInstance();
        queryReq.addNewQuery().setWhereClause(TaskView.ID + "='" + taskId + "'");
        OMElement queryRes = tmsRP.query(genOMElement(queryReq));
        TTaskQueryResultSet resultSet = QueryResponseDocument.Factory.parse(queryRes.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow resultRow = resultSet.getRowArray(0);
        assertEquals(resultRow.getStatusArray(0), TStatus.READY);
        assertTrue(resultRow.getActivationTimeArray(0).getTimeInMillis() > beforeActivateTime);
    }

    public void testNominate() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // nominate the task with only user, the task status will be Reserved
        NominateDocument nominateReq = NominateDocument.Factory.newInstance();
        Nominate nominate = nominateReq.addNewNominate();
        nominate.setIdentifier(taskId);
        TOrganizationalEntity org = TOrganizationalEntity.Factory.newInstance();
        org.addNewUsers().addUser("test_user1");
        nominate.setOrganizationalEntity(org);

        tmsRP.nominate(genOMElement(nominateReq));

        // query the task to check the task status and potentical owners
        QueryDocument queryReq = QueryDocument.Factory.newInstance();
        queryReq.addNewQuery().setWhereClause(TaskView.ID + "='" + taskId + "'");
        OMElement queryRes = tmsRP.query(genOMElement(queryReq));
        TTaskQueryResultSet resultSet = QueryResponseDocument.Factory.parse(queryRes.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow resultRow = resultSet.getRowArray(0);
        assertEquals(resultRow.getStatusArray(0), TStatus.RESERVED);
        TOrganizationalEntity queryOrg = resultRow.getPotentialOwnersArray(0);
        assertEquals(queryOrg.getUsers().getUserArray(0), org.getUsers().getUserArray(0));

        // //////////////////////////////////////////////////////////////////
        // nominate several users
        String taskId2 = createTask("/B4PRequest/create.xml");
        NominateDocument nominateReq2 = NominateDocument.Factory.newInstance();
        Nominate nominate2 = nominateReq2.addNewNominate();
        nominate2.setIdentifier(taskId2);
        TOrganizationalEntity org2 = TOrganizationalEntity.Factory.newInstance();
        TUserlist userList = org2.addNewUsers();
        userList.setUserArray(new String[] { "test_user1", "test_user2" });

        nominate2.setOrganizationalEntity(org2);
        tmsRP.nominate(genOMElement(nominateReq2));

        // query the task to check the task status and potentical owners
        QueryDocument queryReq2 = QueryDocument.Factory.newInstance();
        queryReq2.addNewQuery().setWhereClause(TaskView.ID + "='" + taskId2 + "'");
        OMElement queryRes2 = tmsRP.query(genOMElement(queryReq2));
        TTaskQueryResultSet resultSet2 = QueryResponseDocument.Factory.parse(queryRes2.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow resultRow2 = resultSet2.getRowArray(0);
        assertEquals(resultRow2.getStatusArray(0), TStatus.READY);
        TOrganizationalEntity queryOrg2 = resultRow2.getPotentialOwnersArray(0);

        List expectedList = Arrays.asList(new String[] { "test_user1", "test_user2" });
        assertTrue(expectedList.contains(queryOrg2.getUsers().getUserArray(0)));
        assertTrue(expectedList.contains(queryOrg2.getUsers().getUserArray(1)));
    }

    public void testSetGenericHumanRole() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        String taskId = createTask("/B4PRequest/create.xml");

        // create the request
        SetGenericHumanRoleDocument genDocReq = SetGenericHumanRoleDocument.Factory.newInstance();
        SetGenericHumanRole genDoc = genDocReq.addNewSetGenericHumanRole();
        genDoc.setGenericHumanRole(GenericRoleType.potential_owners.name());
        genDoc.setIdentifier(taskId);
        TOrganizationalEntity org = TOrganizationalEntity.Factory.newInstance();
        TUserlist userList = org.addNewUsers();
        userList.addUser("test_user1");
        userList.addUser("test_user2");
        genDoc.setOrganizationalEntity(org);

        tmsRP.setGenericHumanRole(genOMElement(genDocReq));

        // query the task and to check the potential owners
        QueryDocument queryReq = QueryDocument.Factory.newInstance();
        queryReq.addNewQuery().setWhereClause(TaskView.ID + "='" + taskId + "'");
        OMElement queryRes = tmsRP.query(genOMElement(queryReq));
        TTaskQueryResultSet resultSet = QueryResponseDocument.Factory.parse(queryRes.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow resultRow = resultSet.getRowArray(0);
        TOrganizationalEntity queryOrg = resultRow.getPotentialOwnersArray(0);

        List expectedList = Arrays.asList(new String[] { "test_user1", "test_user2" });
        assertTrue(expectedList.contains(queryOrg.getUsers().getUserArray(0)));
        assertTrue(expectedList.contains(queryOrg.getUsers().getUserArray(1)));

        // //////////////////////////////////////////////////////////////////
        // nominate the task actualOwner
        TOrganizationalEntity aoOrg = TOrganizationalEntity.Factory.newInstance();
        aoOrg.addNewUsers().addUser("actual_owner");
        genDoc.setGenericHumanRole(GenericRoleType.actual_owner.name());
        genDoc.setOrganizationalEntity(aoOrg);

        tmsRP.setGenericHumanRole(genOMElement(genDocReq));

        // query the task again to check the actual owner
        OMElement queryRes2 = tmsRP.query(genOMElement(queryReq));
        TTaskQueryResultSet resultSet2 = QueryResponseDocument.Factory.parse(queryRes2.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow resultRow2 = resultSet2.getRowArray(0);
        assertEquals(resultRow2.getActualOwnerArray(0), aoOrg.getUsers().getUserArray(0));
    }

    public void testGetMyTasks() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        Date beforeCreatedDate = new Date(System.currentTimeMillis());
        String taskId = createTask("/B4PRequest/create.xml");

        // create the request
        GetMyTasksDocument myTaskReq = GetMyTasksDocument.Factory.newInstance();
        GetMyTasks myTasks = myTaskReq.addNewGetMyTasks();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String qrtDate = format.format(beforeCreatedDate);
        myTasks.setCreatedOnClause(TaskView.CREATED_ON + ">'" + qrtDate + "'");
        myTasks.setMaxTasks(10);
        myTasks.setStatusArray(new com.intalio.wsHT.api.TStatus.Enum[] { TStatus.CREATED, TStatus.READY, TStatus.RESERVED });
        myTasks.setTaskType(TaskQueryType.ALL.name());
        myTasks.setWhereClause(TaskView.ID + "='" + taskId + "'");
        myTasks.setGenericHumanRole(GenericRoleType.business_administrators.name());

        // query the task, at least one task will be found.
        OMElement myTaskRes = tmsRP.getMyTasks(genOMElement(myTaskReq));
        TTask[] retTasks = GetMyTasksResponseDocument.Factory.parse(myTaskRes.getXMLStreamReader()).getGetMyTasksResponse().getTaskAbstractArray();
        assertTrue(retTasks.length > 0);
        System.out.println("getMyTasks response:" + Utils.toPrettyXML(myTaskRes));
    }

    public void testGetMyTaskAbstracts() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        Date beforeCreatedDate = new Date(System.currentTimeMillis());
        String taskId = createTask("/B4PRequest/create.xml");

        // create the request
        GetMyTaskAbstractsDocument myTaskReq = GetMyTaskAbstractsDocument.Factory.newInstance();
        GetMyTaskAbstracts myTasks = myTaskReq.addNewGetMyTaskAbstracts();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String qrtDate = format.format(beforeCreatedDate);
        myTasks.setCreatedOnClause(TaskView.CREATED_ON + ">'" + qrtDate + "'");
        myTasks.setMaxTasks(10);
        myTasks.setStatusArray(new com.intalio.wsHT.api.TStatus.Enum[] { TStatus.CREATED, TStatus.READY, TStatus.RESERVED });
        myTasks.setTaskType(TaskQueryType.ALL.name());
        myTasks.setGenericHumanRole(GenericRoleType.business_administrators.name());
        myTasks.setWhereClause(TaskView.ID + "='" + taskId + "'");

        // query the task, at least one task will be found.
        OMElement myTaskRes = tmsRP.getMyTaskAbstracts(genOMElement(myTaskReq));
        TTaskAbstract[] retTasks = GetMyTaskAbstractsResponseDocument.Factory.parse(myTaskRes.getXMLStreamReader()).getGetMyTaskAbstractsResponse()
                        .getTaskAbstractArray();
        assertTrue(retTasks.length > 0);
        System.out.println("getMyTaskAbstracts response:" + Utils.toPrettyXML(myTaskRes));
    }

    public void testQuery() throws Exception {
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();

        // create task
        Date beforeCreatedDate = new Date(System.currentTimeMillis());
        String taskId = createTask("/B4PRequest/create.xml");

        // query the task to check the task status and potentical owners
        QueryDocument queryReq = QueryDocument.Factory.newInstance();
        Query qry = queryReq.addNewQuery();

        // select clause;
        String selectClause = TaskView.ID + ", " + TaskView.CREATED_ON + "," + TaskView.BUSINESS_ADMINISTRATORS + "," + TaskView.NAME + ","
                        + TaskView.ATTACHMENTS;
        qry.setSelectClause(selectClause);

        // for the where clause
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String qrtDate = format.format(beforeCreatedDate);
        String whereClause = TaskView.CREATED_ON + ">'" + qrtDate + "'" + " and (" + TaskView.STATUS + "='" + TaskStatus.CREATED + "' or " + TaskView.STATUS
                        + "='" + TaskStatus.READY + "')" + " and " + TaskView.USERID + " in ('intalio\\manager') " + " and " + TaskView.GENERIC_HUMAN_ROLE
                        + "='" + GenericRoleType.business_administrators + "'";

        qry.setWhereClause(whereClause);

        // order by clause
        String orderByClause = TaskView.CREATED_ON + " asc, " + TaskView.NAME;
        qry.setOrderByClause(orderByClause);

        System.out.println("Query select  clause: " + selectClause);
        System.out.println("Query where   clause: " + whereClause);
        System.out.println("Query orderby clause: " + orderByClause);

        OMElement queryRes = tmsRP.query(genOMElement(queryReq));
        TTaskQueryResultSet resultSet = QueryResponseDocument.Factory.parse(queryRes.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow[] resultRows = resultSet.getRowArray();
        assertTrue(resultRows.length > 0);
        System.out.println("query response: " + Utils.toPrettyXML(queryRes));

    }

    private TTaskQueryResultRow getTask(TMSRequestProcessor tmsRP, String taskId) throws AxisFault, XmlException {
        // query the task to check the task status and activate time
        QueryDocument queryReq = QueryDocument.Factory.newInstance();
        queryReq.addNewQuery().setWhereClause(TaskView.ID + "='" + taskId + "'");
        OMElement queryRes = tmsRP.query(genOMElement(queryReq));
        TTaskQueryResultSet resultSet = QueryResponseDocument.Factory.parse(queryRes.getXMLStreamReader()).getQueryResponse().getQuery();

        // check the query result
        TTaskQueryResultRow resultRow = resultSet.getRowArray(0);
        return resultRow;
    }
}
