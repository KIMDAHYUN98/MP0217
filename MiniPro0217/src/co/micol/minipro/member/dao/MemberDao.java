package co.micol.minipro.member.dao;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.micol.minipro.common.DAO;
import co.micol.minipro.common.DbInterface;
import co.micol.minipro.common.EmployeeVO;
import co.micol.minipro.common.Paging;
import co.micol.minipro.member.service.MemberVo;
import oracle.jdbc.OracleTypes;

public class MemberDao extends DAO implements DbInterface<MemberVo> {
	private PreparedStatement psmt;
	private ResultSet rs;

	@Override
	public ArrayList<MemberVo> selectList() {
		// TODO 회원전체리스트를 돌려준다
		ArrayList<MemberVo> list = new ArrayList<MemberVo>();
		String sql = "SELECT * FROM MEMBER";
		MemberVo vo;
		try {
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				vo = new MemberVo();
				vo.setmId(rs.getString("mid"));
				vo.setmName(rs.getString("mname"));
				vo.setmAuth(rs.getString("mauth"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return list;
	}

	@Override
	public MemberVo select(MemberVo vo) { // 한명의 데이터를 검색한다.
		// TODO 한명의 레코드를 찾아오는 메소드
		String sql = "SELECT * FROM MEMBER WHERE MID = ?";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, vo.getmId());
			rs = psmt.executeQuery();
			if (rs.next()) {
				vo.setmAuth(rs.getString("mauth"));
				vo.setmName(rs.getString("mname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return vo;
	}

	@Override
	public int insert(MemberVo vo) {
		String sql = "INSERT INTO MEMBER(MID,MNAME,MPASSWORD) VALUES(?,?,?)";
		int n = 0;
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, vo.getmId());
			psmt.setString(2, vo.getmName());
			psmt.setString(3, vo.getmPassword());
			n = psmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return n;
	}

	@Override
	public int update(MemberVo vo) {
		// TODO 권한, 패스워드만 변경한다.
		String sql = null;
		if (vo.getmPassword() != null) {
			sql = "UPDATE MEMBER SET MPASSWORD=? WHERE MID=?"; // 패스워드 변경
		} else if (vo.getmAuth() != null) {
			sql = "UPDATE MEMBER SET MAUTH=? WHERE MID=?"; // 권한 변경
		}
		int n = 0;
		try {
			psmt = conn.prepareStatement(sql);
			if (vo.getmPassword() != null)
				psmt.setString(1, vo.getmPassword()); // 패스워드 변경될때
			else
				psmt.setString(1, vo.getmAuth()); // 권한
			psmt.setString(2, vo.getmId());
			n = psmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return n;
	}

	@Override
	public int delete(MemberVo vo) {
		// TODO 회원 한명을 삭제
		String sql = "DELETE FROM MEMBER WHERE MID = ?";
		int n = 0;
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, vo.getmId());
			n = psmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return n;
	}

	public boolean isIdCheck(String id) { // id중복체크를 위한 메소드
		boolean bool = true;
		String sql = "SELECT MID FROM MEMBER WHERE MID = ?";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, id);
			rs = psmt.executeQuery();
			if (rs.next()) {
				bool = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return bool;
	}

	public MemberVo login(MemberVo vo) { // 로그인에서 사용한다.
		// TODO 한명의 레코드를 찾아오는 메소드
		String sql = "SELECT * FROM MEMBER WHERE MID = ? AND MPASSWORD = ?";
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, vo.getmId());
			psmt.setString(2, vo.getmPassword());
			rs = psmt.executeQuery();
			if (rs.next()) {
				vo.setmAuth(rs.getString("mauth"));
				vo.setmName(rs.getString("mname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return vo;
	}

	public EmployeeVO getSalaryInfo(int empId, int salary) {
		EmployeeVO resultvo = null;
		try {
			CallableStatement csmt = conn.prepareCall("{ call SAL_HISTORY_PROC(?,?,?) }");
			csmt.setInt(1, empId);
			csmt.setInt(2, salary);
			csmt.registerOutParameter(3, OracleTypes.CURSOR);
			csmt.execute();

			rs = (ResultSet) csmt.getObject(3);
			if (rs.next()) {
				resultvo = new EmployeeVO();
				resultvo.setEmail(rs.getString("email"));
				resultvo.setEmployeeId(rs.getInt("employee_id"));
				resultvo.setFirstName(rs.getString("first_name"));
				resultvo.setHireDate(rs.getString("hire_date"));
				resultvo.setLastName(rs.getString("last_name"));
				resultvo.setSalary(rs.getInt("salary"));

//				System.out.println(rs.getInt(1)); // employee_id
//				System.out.println(rs.getString("first_name"));
//				System.out.println(rs.getInt("salary"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return resultvo;
	}

	public List<EmployeeVO> getPagingList(int page) {
		List<EmployeeVO> list = new ArrayList<>();
		String sql = "select b.* from " //
				+ "(select rownum rn, a.* "//
				+ "from (select * "//
				+ "        from employees "//
				+ "        order by employee_id) a ) b "//
				+ "where b.rn between ? and ?";

		try {
			psmt = conn.prepareStatement(sql);
			int startCnt = 1 + (page - 1) * 10;
			int endCnt = page * 10;
			psmt.setInt(1, startCnt);
			psmt.setInt(2, endCnt);
			rs = psmt.executeQuery();
			while (rs.next()) {
				EmployeeVO resultvo = new EmployeeVO();
				resultvo.setEmail(rs.getString("email"));
				resultvo.setEmployeeId(rs.getInt("employee_id"));
				resultvo.setFirstName(rs.getString("first_name"));
				resultvo.setHireDate(rs.getString("hire_date"));
				resultvo.setLastName(rs.getString("last_name"));
				resultvo.setSalary(rs.getInt("salary"));
				list.add(resultvo);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	public int getTotalCnt() {
		String sql = "select count(*) from employees";
		int totalCnt = 0;
		try {
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			if(rs.next()) {
				totalCnt = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return totalCnt;
	}

	public List<EmployeeVO> getPagingList(Paging paging) {
		List<EmployeeVO> list = new ArrayList<>();
		String sql = "select b.* " //
				+ "from (select rownum rn, a.* " //
				+ "        from (select e.* "//
				+ "                from employees e " //
				+ "                order by e.employee_id " //
				+ "        ) a ) b " //
				+ "where b.rn between ? and ?";
		try {
			psmt = conn.prepareStatement(sql);
			int f = 1 + ((paging.getPageNo() - 1) * paging.getPageSize());
			int t = paging.getPageNo() * paging.getPageSize();
			System.out.println(f + ", " + t);
			psmt.setInt(1, f);
			psmt.setInt(2, t);
			rs = psmt.executeQuery();
			while (rs.next()) {
				EmployeeVO vo = new EmployeeVO();
				vo.setEmail(rs.getString("email"));
				vo.setEmployeeId(rs.getInt("employee_id"));
				vo.setFirstName(rs.getString("first_name"));
				vo.setHireDate(rs.getString("hire_date"));
				vo.setLastName(rs.getString("last_name"));
				vo.setSalary(rs.getInt("salary"));
				list.add(vo);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return list;
	}

	public EmployeeVO getEmpInfo(int empId, int salary) {
		EmployeeVO vo = null;

		try {
			CallableStatement csmt = conn.prepareCall("{ call salary_history_proc(?,?,?) }");
			csmt.setInt(1, empId);
			csmt.setInt(2, salary);
			csmt.registerOutParameter(3, OracleTypes.CURSOR);
			csmt.execute();

			rs = (ResultSet) csmt.getObject(3);
			if (rs.next()) {
				vo = new EmployeeVO();
				vo.setEmployeeId(rs.getInt(1));
				vo.setFirstName(rs.getString("first_name"));
				vo.setLastName(rs.getString("last_name"));
				vo.setSalary(rs.getInt("salary"));
				vo.setEmail(rs.getString("email"));
				vo.setHireDate(rs.getString("hire_date"));

			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			close();
		}
		return vo;
	}

	private void close() {
		try {
			if (rs != null)
				rs.close();
			if (psmt != null)
				psmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
