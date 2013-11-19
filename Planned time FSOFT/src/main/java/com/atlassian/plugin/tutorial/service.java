package com.atlassian.plugin.tutorial;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericDataSourceException;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.jdbc.SQLProcessor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


import static com.atlassian.jira.rest.v1.util.CacheControl.NO_CACHE;

/**
 * Created by IntelliJ IDEA.
 * User: thaivm
 * Date: 6/20/13
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/planned-time")
@AnonymousAllowed
@Produces({ MediaType.APPLICATION_JSON })
public class service {
    @GET
    @Path ("/getTeam")
    public Response getListTeam(){
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<Team> listTeam = new ArrayList<Team>();
        try {
            sql.prepareStatement("select * from AO_AEFED0_TEAM");
            ResultSet rs=  sql.executeQuery();
            while(rs.next()){
                Team team = new Team(rs.getInt("ID"), rs.getString("NAME"));
                listTeam.add(team);
            }
        } catch (GenericDataSourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GenericEntityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Response.ok(listTeam).cacheControl(NO_CACHE).build();
    }

    @GET
    @Path ("/getUser")
    public Response getListUser(){
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<User> listUser = new ArrayList<User>();
        try {
            sql.prepareStatement("select * from CWD_USER");
            ResultSet rs=  sql.executeQuery();
            while(rs.next()){
                User user = new User(rs.getInt("ID"), rs.getString("USER_NAME"));
                listUser.add(user);
            }
        } catch (GenericDataSourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GenericEntityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Response.ok(listUser).cacheControl(NO_CACHE).build();
    }

    @GET
    @Path ("/getProject")
    public Response getListProject(){
        SQLProcessor sql = new SQLProcessor("defaultDS");
        List<Project> listProject = new ArrayList<Project>();
        try {
            sql.prepareStatement("select * from PROJECT");
            ResultSet rs=  sql.executeQuery();
            while(rs.next()){
                Project project = new Project(rs.getInt("ID"), rs.getString("PNAME"));
                listProject.add(project);
            }
        } catch (GenericDataSourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GenericEntityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Response.ok(listProject).cacheControl(NO_CACHE).build();
    }

    @GET
    @Path ("/filter-team")
    public Response getData(@QueryParam("teamId") String teamIdString, @QueryParam("month") String month, @QueryParam("year") String year){
        SQLProcessor sql = new SQLProcessor("defaultDS");

        long sumPlanned = 0;
        Timestamp fromDayOfMonth = Timestamp.valueOf(year + "-" + month + "-01 00:00:00");
        Timestamp toDayOfMonth = Utils.getLastDateInMonth(fromDayOfMonth);
        long sumRequired = 0;
        String teamName = "";
        try {

            sql.prepareStatement("select DISTINCT(CHILD_NAME), NAME " +
                    "from AO_AEFED0_TEAM_TO_MEMBER, AO_AEFED0_TEAM,AO_AEFED0_TEAM_MEMBER,CWD_MEMBERSHIP " +
                    "where AO_AEFED0_TEAM.ID = AO_AEFED0_TEAM_TO_MEMBER.TEAM_ID " +
                    "and AO_AEFED0_TEAM.ID = "+ teamIdString +
                    "and AO_AEFED0_TEAM_MEMBER.ID = AO_AEFED0_TEAM_TO_MEMBER.TEAM_MEMBER_ID " +
                    "and AO_AEFED0_TEAM_MEMBER.MEMBER_TYPE = 'group' " +
                    "and AO_AEFED0_TEAM_MEMBER.ROLE_TYPE  = 'member' " +
                    "and CWD_MEMBERSHIP.PARENT_NAME = AO_AEFED0_TEAM_MEMBER.MEMBER_KEY");
            ResultSet rs=  sql.executeQuery();
            while(rs.next()){
                sumRequired = sumRequired + Utils.getPlanDay(fromDayOfMonth, toDayOfMonth)*8;
                teamName = rs.getString("NAME");
            }

            sql.prepareStatement("select CHILD_NAME " +
                    "from AO_AEFED0_TEAM_TO_MEMBER, AO_AEFED0_TEAM,AO_AEFED0_TEAM_MEMBER,CWD_MEMBERSHIP " +
                    "where AO_AEFED0_TEAM.ID = AO_AEFED0_TEAM_TO_MEMBER.TEAM_ID " +
                    "and AO_AEFED0_TEAM.ID = "+ teamIdString +
                    "and AO_AEFED0_TEAM_MEMBER.ID = AO_AEFED0_TEAM_TO_MEMBER.TEAM_MEMBER_ID " +
                    "and AO_AEFED0_TEAM_MEMBER.MEMBER_TYPE = 'group' " +
                    "and AO_AEFED0_TEAM_MEMBER.ROLE_TYPE  = 'member' " +
                    "and CWD_MEMBERSHIP.PARENT_NAME = AO_AEFED0_TEAM_MEMBER.MEMBER_KEY");
            rs=  sql.executeQuery();

            while(rs.next()){
                String member = rs.getString("CHILD_NAME");
                sql.prepareStatement("select COLLABORATOR, FROM_DATE, TO_DATE, SECONDS  " +
                        "from AO_86ED1B_TIMEPLAN  " +
                        "where YEAR(TO_DATE) >= " + year +
                        "and ( MONTH(FROM_DATE) <= " + month + " and YEAR(FROM_DATE) <= " + year + ") " +
                        "and COLLABORATOR = '" + member + "'");
                ResultSet resultSet=  sql.executeQuery();
                while(resultSet.next()){

                    Timestamp fromDate = resultSet.getTimestamp("FROM_DATE");
                    Timestamp toDate = resultSet.getTimestamp("TO_DATE");
                    long planned = resultSet.getLong("SECONDS")/3600;

                    Timestamp begin = fromDayOfMonth.getTime() >= fromDate.getTime() ? fromDayOfMonth : fromDate;
                    Timestamp end = toDayOfMonth.getTime() >= toDate.getTime() ? toDate : toDayOfMonth;

                    long numberOfDate = Utils.getPlanDay(begin, end);

                    sumPlanned = sumPlanned + planned*numberOfDate;
                }
            }
        } catch (GenericDataSourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (GenericEntityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        TimePlan timePlan = new TimePlan(teamName, fromDayOfMonth, toDayOfMonth, sumPlanned, sumRequired);
        return Response.ok(timePlan).cacheControl(NO_CACHE).build();
    }

    @XmlRootElement
    public static class TimePlan{
        @XmlElement
        private String Name;
		@XmlElement
        private Timestamp begin;
        @XmlElement
        private Timestamp end;
        @XmlElement
        private long sumPlanned;
        @XmlElement
        private long sumRequired;
        TimePlan(final String name, final Timestamp begin, final Timestamp end, final long sumPlanned, final long sumRequired){
            this.Name = name;
            this.begin = begin;
            this.end = end;
            this.setSumPlanned(sumPlanned);
            this.setSumRequired(sumRequired);
        }
        public String getName() {
			return Name;
		}
		public void setName(String name) {
			Name = name;
		}
		public Timestamp getBegin() {
			return begin;
		}
		public void setBegin(Timestamp begin) {
			this.begin = begin;
		}
		public Timestamp getEnd() {
			return end;
		}
		public void setEnd(Timestamp end) {
			this.end = end;
		}
		public long getSumRequired() {
			return sumRequired;
		}
		public void setSumRequired(long sumRequired) {
			this.sumRequired = sumRequired;
		}
		public long getSumPlanned() {
			return sumPlanned;
		}
		public void setSumPlanned(long sumPlanned) {
			this.sumPlanned = sumPlanned;
		}
    }

    @XmlRootElement
    public  static class User{
        @XmlElement
        private int id;

        @XmlElement
        private String name;

        User(final int id, String name){
            this.id = id;
            this.name = name;
        }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
        
    }

    @XmlRootElement
    public  static class Team{
        @XmlElement
        private int id;

        @XmlElement
        private String name;

        Team(final int id, String name){
            this.id = id;
            this.name = name;
        }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    }

    @XmlRootElement
    public  static class Project{
        @XmlElement
        private int id;

        @XmlElement
        private String name;

        Project(final int id, String name){
            this.id = id;
            this.name = name;
        }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    }

}
