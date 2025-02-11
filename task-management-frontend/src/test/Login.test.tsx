import { render, screen, fireEvent, waitFor } from "@testing-library/react"
import "@testing-library/jest-dom"
import { BrowserRouter } from "react-router-dom"
import { AuthProvider } from "../components/AuthContext.tsx"
import Login from "../components/Login.tsx"
import axios from "axios"

jest.mock('axios', () => ({
    __esModule: true,
    default: {
        post: jest.fn(() => Promise.resolve({ data: {} })),
    },
}));
const mockedAxios = axios as jest.Mocked<typeof axios>

const renderLogin = () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <Login />
            </AuthProvider>
        </BrowserRouter>,
    )
}

describe("Login Component", () => {
    test("renders login form", () => {
        renderLogin()
        expect(screen.getByLabelText(/username/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument()
        expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument()
    })

    test("handles successful login", async () => {
        mockedAxios.post.mockResolvedValueOnce({ data: { token: "fake-token" } })
        renderLogin()

        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: "testuser" } })
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "password123" } })
        fireEvent.click(screen.getByRole("button", { name: /login/i }))

        await waitFor(() => {
            expect(mockedAxios.post).toHaveBeenCalledWith("http://localhost:8080/auth/login", {
                username: "testuser",
                password: "password123",
            })
        })
    })

    test("handles login failure", async () => {
        const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        (mockedAxios.post as jest.Mock).mockRejectedValueOnce(new Error("Invalid credentials"));

        renderLogin();
        fireEvent.change(screen.getByLabelText(/username/i), { target: { value: "wronguser" } });
        fireEvent.change(screen.getByLabelText(/password/i), { target: { value: "wrongpassword" } });
        fireEvent.click(screen.getByRole("button", { name: /login/i }));
        await waitFor(() => {
            expect(mockedAxios.post).toHaveBeenCalledWith(
                "http://localhost:8080/auth/login",
                {
                    username: "wronguser",
                    password: "wrongpassword",
                }
            );
        });

        expect(consoleErrorSpy).toHaveBeenCalledWith("Login failed:", expect.any(Error));
        consoleErrorSpy.mockRestore();
    });
})

