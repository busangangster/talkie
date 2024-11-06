import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { updateUserInfo } from "../apis/api";
import useUserStore from "../store/useUserStore";

const InfoPage: React.FC = () => {
  const [formData, setFormData] = useState({
    name: "",
    age: 0,
    gender: "",
    favorite: "",
    remark: "",
  });
  const [errorField, setErrorField] = useState("");
  const navigate = useNavigate();
  const { userSeq } = useUserStore();

  const calculateKoreanAge = (birthDate: string) => {
    const birthYear = new Date(birthDate).getFullYear();
    const currentYear = new Date().getFullYear();
    return currentYear - birthYear + 1;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    if (name === "age") {
      const age = calculateKoreanAge(value);
      setFormData((prevData) => ({ ...prevData, age }));
    } else {
      setFormData((prevData) => ({ ...prevData, [name]: value }));
    }
    setErrorField("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!userSeq) {
      console.error("userSeq가 설정되지 않았습니다.");
      return;
    }

    if (!formData.name.trim()) {
      setErrorField("name");
    } else if (formData.age <= 0) {
      setErrorField("age");
    } else if (!formData.gender.trim()) {
      setErrorField("gender");
    } else if (!formData.favorite.trim()) {
      setErrorField("favorite");
    } else {
      setErrorField("");

      try {
        const genderCode = formData.gender === "남성" ? "M" : "F";

        const response = await updateUserInfo({
          userSeq,
          ...formData,
          gender: genderCode,
        });

        if (response) {
          navigate("/home");
        }
      } catch (error) {
        console.error("API 요청 중 오류 발생:", error);
      }
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="bg-white bg-opacity-80 rounded-xl shadow-lg text-center w-2/5 px-28 py-12">
        <h2 className="text-[35px] font-bold mb-6">우리 아이 정보 입력</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="이름"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "name" ? "이름을 입력해주세요." : ""}
            </p>
          </div>

          <div className="space-y-2">
            <input
              type="date"
              name="age"
              onChange={(e) => handleChange(e)}
              placeholder="생년월일"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF] placeholder-gray-400"
              style={{
                boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)",
                color: "inherit",
              }}
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "age" ? "생년월일을 입력해주세요." : ""}
            </p>
          </div>

          <div className="flex space-x-4">
            <div className="flex-1 flex space-x-2">
              <label className="flex items-center flex-1">
                <input
                  type="radio"
                  name="gender"
                  value="남성"
                  checked={formData.gender === "남성"}
                  onChange={handleChange}
                  className="hidden peer"
                />
                <span
                  className={`peer-checked:bg-[#869FD3] peer-checked:text-white bg-[#D7E5EF] w-full text-gray-700 py-2 px-4 rounded-lg cursor-pointer text-center shadow-md`}
                >
                  남성
                </span>
              </label>
              <label className="flex items-center flex-1">
                <input
                  type="radio"
                  name="gender"
                  value="여성"
                  checked={formData.gender === "여성"}
                  onChange={handleChange}
                  className="hidden peer"
                />
                <span
                  className={`peer-checked:bg-[#869FD3] peer-checked:text-white bg-[#D7E5EF] w-full text-gray-700 py-2 px-4 rounded-lg cursor-pointer text-center shadow-md`}
                >
                  여성
                </span>
              </label>
            </div>
          </div>

          <p className="text-red-500 text-xs text-left ml-2 h-1">
            {errorField === "gender" && "성별을 선택해주세요."}
          </p>

          <div className="space-y-2">
            <input
              type="text"
              name="favorite"
              value={formData.favorite}
              onChange={handleChange}
              placeholder="좋아하는 것"
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
            <p className="text-red-500 text-xs text-left ml-2 h-1">
              {errorField === "favorite" ? "좋아하는 색을 입력해주세요." : ""}
            </p>
          </div>

          <div className="space-y-2">
            <input
              type="text"
              name="remark"
              value={formData.remark}
              onChange={handleChange}
              placeholder="특이사항"
              className="w-full px-4 py-2 mb-6 border rounded-lg focus:outline-none focus:border-gray-600 bg-[#D7E5EF]"
              style={{ boxShadow: "inset 0 2px 4px rgba(0, 0, 0, 0.2)" }}
            />
          </div>

          <button
            type="submit"
            className="w-full bg-[#708BA0] hover:bg-[#61a0d0] text-white font-semibold py-2 rounded-xl "
          >
            입력 완료
          </button>
        </form>
      </div>
    </div>
  );
};

export default InfoPage;
